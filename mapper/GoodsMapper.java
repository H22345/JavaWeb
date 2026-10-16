package SHmarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import SHmarketplace.entity.Goods;

/**
 * 商品数据访问接口
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 下架/上架商品（修改状态）
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 扣减库存 + 增加销量
     */
    int deductStock(@Param("goodsId") Long goodsId, @Param("quantity") Integer quantity);

    /**
     * 按分类分页查询商品列表
     */
    IPage<Goods> selectByCategory(Page<Goods> page, @Param("categoryId") Long categoryId, @Param("status") Integer status);

    /**
     * 根据商品名分页模糊搜索
     */
    IPage<Goods> searchByName(Page<Goods> page, @Param("keyword") String keyword, @Param("status") Integer status);
}