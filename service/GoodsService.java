package SHmarketplace.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import SHmarketplace.dto.GoodsCreateDTO;
import SHmarketplace.entity.Goods;

import java.math.BigDecimal;

/**
 * 商品业务接口
 */
public interface GoodsService {

    /**
     * 创建商品（上架）
     *
     * @param dto      商品信息（前端传入）
     * @param sellerId 卖家用户ID（当前登录用户，不由前端传入）
     */
    Goods createGoods(@Valid GoodsCreateDTO dto, Long sellerId);

    /**
     * 根据 ID 查询商品（可能返回 null）
     */
    Goods findById(Long id);

    /**
     * 下架商品
     */
    void offShelf(Long id);

    /**
     * 上架商品
     */
    void onShelf(Long id);

    /**
     * 直接更新库存和销量（用于订单取消回滚库存）
     */
    void updateStockAndSales(Goods goods);

    /**
     * 分页查询全部在售商品
     *
     * @param pageNum  页码（从 1 开始）
     * @param pageSize 每页条数（最大 100）
     */
    IPage<Goods> listAll(@Min(1) Integer pageNum,
                         @Min(1) @Max(100) Integer pageSize);

    /**
     * 按分类分页查询在售商品
     *
     * @param pageNum  页码（从 1 开始）
     * @param pageSize 每页条数（最大 100）
     */
    IPage<Goods> listByCategory(Long categoryId,
                                @Min(1) Integer pageNum,
                                @Min(1) @Max(100) Integer pageSize);

    /**
     * 按关键字分页搜索在售商品
     */
    IPage<Goods> searchByName(@NotBlank String keyword,
                              @Min(1) Integer pageNum,
                              @Min(1) @Max(100) Integer pageSize);

    /**
     * 扣减库存 + 增加销量（下单时调用）
     * @return true 扣减成功，false 库存不足
     */
    boolean deductStock(@NotNull Long goodsId, @Min(1) Integer quantity);

    /**
     * 更新商品价格（必须 > 0）
     */
    void updatePrice(Long id, @DecimalMin(value = "0.01") BigDecimal price);
}