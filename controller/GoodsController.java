package SHmarketplace.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import SHmarketplace.annotation.CurrentUser;
import SHmarketplace.common.Result;
import SHmarketplace.dto.GoodsCreateDTO;
import SHmarketplace.entity.Goods;
import SHmarketplace.exception.BusinessException;
import SHmarketplace.exception.ErrorCode;
import SHmarketplace.service.GoodsService;

import java.math.BigDecimal;

/**
 * 商品控制器
 */
@Tag(name = "商品管理", description = "商品的增删改查、上下架、搜索等")
@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    @Operation(summary = "创建商品（上架）")
    @PostMapping
    public Result<Goods> createGoods(@Valid @RequestBody GoodsCreateDTO dto,
                                     @CurrentUser Long userId) {
        Goods goods = goodsService.createGoods(dto, userId);
        return Result.success(goods);
    }

    @Operation(summary = "根据 ID 查询商品")
    @GetMapping("/{id}")
    public Result<Goods> getById(@PathVariable Long id) {
        Goods goods = goodsService.findById(id);
        if (goods == null) {
            throw new BusinessException(ErrorCode.GOODS_NOT_FOUND);
        }
        return Result.success(goods);
    }

    @Operation(summary = "下架商品")
    @PostMapping("/{id}/off-shelf")
    public Result<Void> offShelf(@PathVariable Long id) {
        goodsService.offShelf(id);
        return Result.success();
    }

    @Operation(summary = "上架商品")
    @PostMapping("/{id}/on-shelf")
    public Result<Void> onShelf(@PathVariable Long id) {
        goodsService.onShelf(id);
        return Result.success();
    }

    @Operation(summary = "分页查询全部在售商品")
    @GetMapping
    public Result<IPage<Goods>> listAll(
            @Parameter(description = "页码，从 1 开始")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数，最大 100")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(goodsService.listAll(pageNum, pageSize));
    }

    @Operation(summary = "按分类分页查询在售商品")
    @GetMapping("/category/{categoryId}")
    public Result<IPage<Goods>> listByCategory(
            @PathVariable Long categoryId,
            @Parameter(description = "页码，从 1 开始")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数，最大 100")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(goodsService.listByCategory(categoryId, pageNum, pageSize));
    }

    @Operation(summary = "按关键字分页搜索在售商品")
    @GetMapping("/search")
    public Result<IPage<Goods>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(goodsService.searchByName(keyword, pageNum, pageSize));
    }

    @Operation(summary = "更新商品价格")
    @PutMapping("/{id}/price")
    public Result<Void> updatePrice(@PathVariable Long id,
                                    @RequestParam BigDecimal price) {
        goodsService.updatePrice(id, price);
        return Result.success();
    }
}