package SHmarketplace.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import SHmarketplace.entity.Category;

import java.util.List;

/**
 * 分类业务接口
 */
public interface CategoryService {

    /**
     * 创建分类
     */
    Category createCategory(@Valid Category category);

    /**
     * 更新分类
     */
    void updateCategory(@Valid Category category);

    /**
     * 删除分类（逻辑删除，将 status 置 0）
     * 如果该分类下还有子分类或关联商品，需要先处理
     */
    void deleteCategory(@NotNull Long id);

    /**
     * 根据 ID 查询分类
     */
    Category findById(@NotNull Long id);

    /**
     * 查询所有顶级分类（parent_id=0）
     */
    List<Category> listTopLevel();

    /**
     * 按父分类 ID 查询子分类（包含父级自身）
     */
    List<Category> listByParentId(@NotNull Long parentId);

    /**
     * 一次性组装完整分类树（顶级分类 + 每个顶级分类下挂载子分类）
     */
    List<Category> getCategoryTree();
}