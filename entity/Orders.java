package SHmarketplace.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 映射数据库表：orders
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("orders")
public class Orders implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 订单号，业务唯一编号，不自增
     */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 卖家id
     */
    private Integer sellerId;

    /**
     * 买家id
     */
    private Integer buyerId;

    /**
     * 成交价格
     */
    private BigDecimal payPrice;

    /**
     * 订单状态：1待付款 2已付款 3交易完成 4已取消
     */
    private Integer orderStatus;

    /**
     * 下单时间（自动填充）
     */
    private LocalDateTime createTime;

    /**
     * 更新时间（状态变更时间，自动填充）
     */
    private LocalDateTime updateTime;
}