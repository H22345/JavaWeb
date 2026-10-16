package SHmarketplace.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import SHmarketplace.annotation.CurrentUser;
import SHmarketplace.common.Result;
import SHmarketplace.dto.OrderCreateDTO;
import SHmarketplace.entity.Orders;
import SHmarketplace.exception.BusinessException;
import SHmarketplace.exception.ErrorCode;
import SHmarketplace.service.OrdersService;

/**
 * 订单控制器
 */
@Tag(name = "订单管理", description = "下单、付款、完成、取消等订单流转")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;

    @Operation(summary = "下单")
    @PostMapping
    public Result<Orders> placeOrder(@Valid @RequestBody OrderCreateDTO dto,
                                     @CurrentUser Long userId) {
        Orders order = ordersService.placeOrder(userId.intValue(), dto);
        return Result.success(order);
    }

    @Operation(summary = "支付订单")
    @PostMapping("/{orderId}/pay")
    public Result<Void> payOrder(@PathVariable String orderId) {
        ordersService.payOrder(orderId);
        return Result.success();
    }

    @Operation(summary = "完成交易")
    @PostMapping("/{orderId}/finish")
    public Result<Void> finishOrder(@PathVariable String orderId) {
        ordersService.finishOrder(orderId);
        return Result.success();
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(@PathVariable String orderId) {
        ordersService.cancelOrder(orderId);
        return Result.success();
    }

    @Operation(summary = "根据订单号查询")
    @GetMapping("/{orderId}")
    public Result<Orders> getById(@PathVariable String orderId) {
        Orders order = ordersService.findById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        return Result.success(order);
    }

    @Operation(summary = "分页查询买家订单列表")
    @GetMapping("/buyer")
    public Result<IPage<Orders>> listByBuyer(
            @CurrentUser Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(ordersService.listByBuyer(userId.intValue(), pageNum, pageSize));
    }

    @Operation(summary = "分页查询卖家订单列表")
    @GetMapping("/seller")
    public Result<IPage<Orders>> listBySeller(
            @CurrentUser Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(ordersService.listBySeller(userId.intValue(), pageNum, pageSize));
    }
}