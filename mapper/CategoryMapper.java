package SHmarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import SHmarketplace.entity.Category;

import java.util.List;

/**
 * 分类数据访问接口
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 按父分类ID查询启用状态的子分类（按 sort 降序）
     *
     * @param parentId 父分类ID（传 0 查顶级分类）
     */
    List<Category> selectChildrenByParentId(@Param("parentId") Long parentId);

    /**
     * 查询所有启用的顶级分类
     */
    List<Category> selectAllTopLevel();
}