package SHmarketplace.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import SHmarketplace.common.Result;
import SHmarketplace.entity.Category;
import SHmarketplace.exception.BusinessException;
import SHmarketplace.exception.ErrorCode;
import SHmarketplace.service.CategoryService;

import java.util.List;

/**
 * 商品分类控制器
 */
@Tag(name = "分类管理", description = "商品分类的增删改查、父子关系管理")
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "创建分类")
    @PostMapping
    public Result<Category> createCategory(@Valid @RequestBody Category category) {
        return Result.success(categoryService.createCategory(category));
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody Category category) {
        category.setId(id);
        categoryService.updateCategory(category);
        return Result.success();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }

    @Operation(summary = "根据ID查询分类")
    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        return Result.success(category);
    }

    @Operation(summary = "查询所有顶级分类")
    @GetMapping("/top")
    public Result<List<Category>> listTopLevel() {
        return Result.success(categoryService.listTopLevel());
    }

    @Operation(summary = "按父分类ID查询子分类")
    @GetMapping("/children")
    public Result<List<Category>> listChildren(
            @Parameter(description = "父分类ID，传 0 查顶级分类")
            @RequestParam(defaultValue = "0") Long parentId) {
        return Result.success(categoryService.listByParentId(parentId));
    }

    @Operation(summary = "获取完整分类结构（顶级 + 子分类扁平列表）")
    @GetMapping("/tree")
    public Result<List<Category>> getCategoryTree() {
        return Result.success(categoryService.getCategoryTree());
    }
}