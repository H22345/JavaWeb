package SHmarketplace.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import SHmarketplace.dto.OrderCreateDTO;
import SHmarketplace.entity.Orders;

/**
 * 订单业务接口
 */
public interface OrdersService {

    /**
     * 下单（事务：扣库存 + 写订单）
     *
     * @param buyerId 买家ID（从登录态获取）
     */
    Orders placeOrder(@NotNull Integer buyerId, @Valid OrderCreateDTO dto);

    /**
     * 支付订单（待付款 → 已付款）
     */
    void payOrder(@NotBlank String orderId);

    /**
     * 完成订单（已付款 → 交易完成）
     */
    void finishOrder(@NotBlank String orderId);

    /**
     * 取消订单（待付款 → 已取消）
     */
    void cancelOrder(@NotBlank String orderId);

    /**
     * 根据订单号查询（可能返回 null）
     */
    Orders findById(@NotBlank String orderId);

    /**
     * 分页查询买家的订单列表
     */
    IPage<Orders> listByBuyer(@NotNull Integer buyerId,
                              @Min(1) Integer pageNum,
                              @Min(1) @Max(100) Integer pageSize);

    /**
     * 分页查询卖家的订单列表
     */
    IPage<Orders> listBySeller(@NotNull Integer sellerId,
                               @Min(1) Integer pageNum,
                               @Min(1) @Max(100) Integer pageSize);
}