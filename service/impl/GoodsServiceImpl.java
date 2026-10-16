package SHmarketplace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import SHmarketplace.dto.GoodsCreateDTO;
import SHmarketplace.entity.Goods;
import SHmarketplace.exception.BusinessException;
import SHmarketplace.exception.ErrorCode;
import SHmarketplace.mapper.GoodsMapper;
import SHmarketplace.service.GoodsService;

import java.math.BigDecimal;

/**
 * 商品业务实现
 */
@Service
@Validated
@RequiredArgsConstructor
public class GoodsServiceImpl implements GoodsService {

    /** 商品状态：1-在售，0-下架 */
    private static final int STATUS_ON_SHELF = 1;
    private static final int STATUS_OFF_SHELF = 0;

    private final GoodsMapper goodsMapper;

    @Override
    public Goods createGoods(GoodsCreateDTO dto, Long sellerId) {
        // 1. 组装实体（sellerId 从方法参数传入，由 Controller 层从登录态获取）
        Goods goods = Goods.builder()
                .sellerId(sellerId)
                .goodsName(dto.getGoodsName())
                .goodsTitle(dto.getGoodsTitle())
                .price(dto.getPrice())
                .costPrice(dto.getCostPrice())
                .categoryId(dto.getCategoryId())
                .stock(dto.getStock())
                .sales(0)
                .goodsImg(dto.getGoodsImg())
                .status(STATUS_ON_SHELF)
                .build();

        // 3. 插入
        int rows = goodsMapper.insert(goods);
        if (rows == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "商品创建失败");
        }
        return goods;
    }

    @Override
    public void updateStockAndSales(Goods goods) {
        int rows = goodsMapper.updateById(goods);
        if (rows == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "库存回滚失败");
        }
    }

    @Override
    public Goods findById(Long id) {
        return goodsMapper.selectById(id);
    }

    @Override
    public void offShelf(Long id) {
        assertGoodsExists(id);
        int rows = goodsMapper.updateStatus(id, STATUS_OFF_SHELF);
        if (rows == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下架失败");
        }
    }

    @Override
    public void onShelf(Long id) {
        assertGoodsExists(id);
        int rows = goodsMapper.updateStatus(id, STATUS_ON_SHELF);
        if (rows == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上架失败");
        }
    }

    @Override
    public IPage<Goods> listAll(Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getStatus, STATUS_ON_SHELF)
               .orderByDesc(Goods::getSales);
        return goodsMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public IPage<Goods> listByCategory(Long categoryId, Integer pageNum, Integer pageSize) {
        return goodsMapper.selectByCategory(new Page<>(pageNum, pageSize), categoryId, STATUS_ON_SHELF);
    }

    @Override
    public IPage<Goods> searchByName(String keyword, Integer pageNum, Integer pageSize) {
        return goodsMapper.searchByName(new Page<>(pageNum, pageSize), keyword, STATUS_ON_SHELF);
    }

    @Override
    public boolean deductStock(Long goodsId, Integer quantity) {
        // 用 MP 的条件更新：只有 stock >= quantity 才扣减
        LambdaQueryWrapper<Goods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Goods::getId, goodsId)
               .ge(Goods::getStock, quantity);

        Goods update = new Goods();
        update.setStock(null);   // 不直接 set，改用 XML 的 stock - #{quantity}
        // 实际调用 XML 写的方法（更精确）
        return goodsMapper.deductStock(goodsId, quantity) > 0;
    }

    @Override
    public void updatePrice(Long id, BigDecimal price) {
        Goods goods = assertGoodsExists(id);
        // 深拷贝只更新价格字段，其他字段不动
        Goods update = new Goods();
        update.setId(id);
        update.setPrice(price);
        if (goodsMapper.updateById(update) == 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "价格更新失败");
        }
    }

    /**
     * 内部方法：断言商品存在，不存在则抛异常
     */
    private Goods assertGoodsExists(Long id) {
        Goods goods = goodsMapper.selectById(id);
        if (goods == null) {
            throw new BusinessException(ErrorCode.GOODS_NOT_FOUND);
        }
        return goods;
    }
}