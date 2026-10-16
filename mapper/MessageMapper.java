package SHmarketplace.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import SHmarketplace.entity.Message;

import java.util.List;

/**
 * 留言数据访问接口
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 查询买家对某商品的所有留言
     */
    List<Message> selectByGoodsId(@Param("goodsId") Integer goodsId);

    /**
     * 查询卖家收到的所有留言
     */
    List<Message> selectBySellerId(@Param("toUserId") Integer toUserId);

    /**
     * 卖家回复留言
     */
    int replyMessage(@Param("id") Integer id, @Param("reply") String reply);

    /**
     * 查询买家的历史留言
     */
    List<Message> selectByFromUserId(@Param("fromUserId") Integer fromUserId);
}