package SHmarketplace.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import SHmarketplace.entity.Category;
import SHmarketplace.exception.BusinessException;
import SHmarketplace.exception.ErrorCode;
import SHmarketplace.mapper.CategoryMapper;
import SHmarketplace.service.CategoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类业务实现
 */
@Service
@Validated
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public Category createCategory(Category category) {
        // 1. 校验父分类存在性（如果不是顶级）
        if (category.getParentId() != null && category.getParentId() != 0) {
            Category parent = categoryMapper.selectById(category.getParentId());
            if (parent == null) {
                throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND, "父分类不存在");
            }
        }
        // 2. 默认值兜底
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        // 3. 插入
        categoryMapper.insert(category);
        return category;
    }

    @Override
    public void updateCategory(Category category) {
        assertCategoryExists(category.getId());
        categoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = assertCategoryExists(id);
        // 如果有子分类，先递归删除子分类
        List<Category> children = categoryMapper.selectChildrenByParentId(id);
        for (Category child : children) {
            deleteCategory(child.getId());
        }
        // 逻辑删除：把 status 置 0
        Category update = new Category();
        update.setId(id);
        update.setStatus(0);
        categoryMapper.updateById(update);
    }

    @Override
    public Category findById(Long id) {
        return categoryMapper.selectById(id);
    }

    @Override
    public List<Category> listTopLevel() {
        return categoryMapper.selectAllTopLevel();
    }

    @Override
    public List<Category> listByParentId(Long parentId) {
        return categoryMapper.selectChildrenByParentId(parentId);
    }

    @Override
    public List<Category> getCategoryTree() {
        // 1. 查所有启用的顶级分类
        List<Category> tops = categoryMapper.selectAllTopLevel();
        // 2. 查所有启用的子分类
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, 1);
        List<Category> allChildren = categoryMapper.selectList(wrapper);
        // 3. 用 Map 按 parent_id 分组
        Map<Long, List<Category>> childrenMap = new HashMap<>();
        for (Category c : allChildren) {
            // 跳过顶级（parent_id=0）
            if (c.getParentId() != null && c.getParentId() != 0) {
                childrenMap.computeIfAbsent(c.getParentId(), k -> new ArrayList<>()).add(c);
            }
        }
        // 4. 组装树 —— 顶级分类 + 其子分类
        // 这里简单返回扁平结构（List<Category> 包含顶级 + 子级），
        // Controller 层也可以转成树状 JSON
        List<Category> result = new ArrayList<>();
        for (Category top : tops) {
            result.add(top);
            List<Category> children = childrenMap.get(top.getId());
            if (children != null) {
                result.addAll(children);
            }
        }
        return result;
    }

    /**
     * 内部方法：断言分类存在
     */
    private Category assertCategoryExists(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        return category;
    }
}