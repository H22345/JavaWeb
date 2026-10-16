package SHmarketplace.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import SHmarketplace.dto.OrderCreateDTO;
import SHmarketplace.entity.Goods;
import SHmarketplace.entity.Orders;
import SHmarketplace.entity.User;
import SHmarketplace.exception.BusinessException;
import SHmarketplace.exception.ErrorCode;
import SHmarketplace.mapper.OrdersMapper;
import SHmarketplace.service.GoodsService;
import SHmarketplace.service.OrdersService;
import SHmarketplace.service.UserService;

import java.util.UUID;

/**
 * 订单业务实现
 */
@Service
@Validated
@RequiredArgsConstructor
public class OrdersServiceImpl implements OrdersService {

    /** 订单状态常量 */
    public static final int STATUS_PENDING_PAYMENT = 1;  // 待付款
    public static final int STATUS_PAID = 2;            // 已付款
    public static final int STATUS_COMPLETED = 3;        // 交易完成
    public static final int STATUS_CANCELLED = 4;        // 已取消

    /** 单次下单数量（默认1件，扩展可改） */
    private static final int DEFAULT_QUANTITY = 1;

    private final OrdersMapper ordersMapper;
    private final GoodsService goodsService;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders placeOrder(Integer buyerId, OrderCreateDTO dto) {
        // 1. 校验买家存在
        User buyer = userService.findById((long) buyerId);
        if (buyer == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "买家不存在");
        }

        // 2. 校验商品存在且在售
        Goods goods = goodsService.findById((long) dto.getGoodsId());
        if (goods == null) {
            throw new BusinessException(ErrorCode.GOODS_NOT_FOUND);
        }
        if (goods.getStatus() != 1) {
            throw new BusinessException(ErrorCode.GOODS_OFF_SHELF);
        }

        // 3. 校验库存
        if (goods.getStock() < DEFAULT_QUANTITY) {
            throw new BusinessException(ErrorCode.STOCK_NOT_ENOUGH);
        }

        // 4. 校验成交价格合理性（不能高于售价，允许折扣）
        if (dto.getPayPrice().compareTo(goods.getPrice()) > 0) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "成交价格不能高于商品售价");
        }

        // 5. 从商品上获取真实的卖家ID（商品发布者）
        Integer sellerId = goods.getSellerId().intValue();

        // 额外校验：买家不能买自己的商品
        if (sellerId.equals(buyerId)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "不能购买自己发布的商品");
        }

        // 6. 扣减库存（数据库乐观锁式更新：stock >= quantity 才扣）
        boolean deducted = goodsService.deductStock((long) dto.getGoodsId(), DEFAULT_QUANTITY);
        if (!deducted) {
            throw new BusinessException(ErrorCode.STOCK_NOT_ENOUGH, "库存不足，可能已被抢先下单");
        }

        // 7. 生成订单号（32位，与数据库 varchar(32) 对齐）
        String orderId = generateOrderId();

        // 8. 组装订单 + 插入
        Orders order = Orders.builder()
                .id(orderId)
                .goodsId(dto.getGoodsId())
                .sellerId(sellerId)
                .buyerId(buyerId)
                .payPrice(dto.getPayPrice())
                .orderStatus(STATUS_PENDING_PAYMENT)
                .build();

        int rows = ordersMapper.insert(order);
        if (rows == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "订单创建失败");
        }

        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(String orderId) {
        Orders order = assertOrderExists(orderId);

        if (order.getOrderStatus() != STATUS_PENDING_PAYMENT) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID,
                    "当前状态不允许付款操作");
        }

        ordersMapper.updateStatus(orderId, STATUS_PAID);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishOrder(String orderId) {
        Orders order = assertOrderExists(orderId);

        if (order.getOrderStatus() != STATUS_PAID) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID,
                    "只有已付款订单才能完成交易");
        }

        ordersMapper.updateStatus(orderId, STATUS_COMPLETED);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderId) {
        Orders order = assertOrderExists(orderId);

        if (order.getOrderStatus() != STATUS_PENDING_PAYMENT) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID,
                    "只有待付款订单才能取消");
        }

        // 取消 = 回滚库存
        Goods goods = goodsService.findById((long) order.getGoodsId());
        if (goods == null) {
            throw new BusinessException(ErrorCode.GOODS_NOT_FOUND);
        }
        // 库存 +1，销量 -1（对称补偿）
        Goods rollback = new Goods();
        rollback.setId((long) goods.getId());
        rollback.setStock(goods.getStock() + DEFAULT_QUANTITY);
        rollback.setSales(Math.max(0, goods.getSales() - DEFAULT_QUANTITY));
        goodsService.updateStockAndSales(rollback);   // 这个方法我在 GoodsService 里补一下

        ordersMapper.updateStatus(orderId, STATUS_CANCELLED);
    }

    @Override
    public Orders findById(String orderId) {
        return ordersMapper.selectById(orderId);
    }

    @Override
    public IPage<Orders> listByBuyer(Integer buyerId, Integer pageNum, Integer pageSize) {
        return ordersMapper.selectByBuyerId(new Page<>(pageNum, pageSize), buyerId);
    }

    @Override
    public IPage<Orders> listBySeller(Integer sellerId, Integer pageNum, Integer pageSize) {
        return ordersMapper.selectBySellerId(new Page<>(pageNum, pageSize), sellerId);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 断言订单存在
     */
    private Orders assertOrderExists(String orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        return order;
    }

    /**
     * 生成 32 位订单号
     */
    private String generateOrderId() {
        // UUID 去掉横线后正好 32 位
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}