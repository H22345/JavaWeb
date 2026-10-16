package SHmarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import SHmarketplace.entity.Orders;

/**
 * 订单数据访问接口
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

    /**
     * 根据买家ID分页查询订单列表
     */
    IPage<Orders> selectByBuyerId(Page<Orders> page, @Param("buyerId") Integer buyerId);

    /**
     * 根据卖家ID分页查询订单列表
     */
    IPage<Orders> selectBySellerId(Page<Orders> page, @Param("sellerId") Integer sellerId);

    /**
     * 更新订单状态（付款/取消/完成）
     */
    int updateStatus(@Param("orderId") String orderId, @Param("orderStatus") Integer orderStatus);

    /**
     * 根据订单号查询订单
     */
    Orders selectById(@Param("orderId") String orderId);
}