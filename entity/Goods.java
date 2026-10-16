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
 * 商品实体类
 * 映射数据库表：goods
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("goods")
public class Goods implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品副标题
     */
    private String goodsTitle;

    /**
     * 售价
     */
    private BigDecimal price;

    /**
     * 成本价
     */
    private BigDecimal costPrice;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 卖家用户ID（商品发布者）
     */
    private Long sellerId;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 销量
     */
    private Integer sales;

    /**
     * 商品主图
     */
    private String goodsImg;

    /**
     * 商品状态
     */
    private Integer status;

    /**
     * 创建时间（上架时间，自动填充）
     */
    private LocalDateTime createTime;

    /**
     * 更新时间（自动填充）
     */
    private LocalDateTime updateTime;

}