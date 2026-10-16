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
import java.time.LocalDateTime;

/**
 * 留言实体类
 * 映射数据库表：message
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("message")
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 留言人(买家)
     */
    private Integer fromUserId;

    /**
     * 接收人(卖家)
     */
    private Integer toUserId;

    /**
     * 留言内容
     */
    private String content;

    /**
     * 卖家回复内容
     */
    private String reply;

    /**
     * 留言时间（自动填充）
     */
    private LocalDateTime createTime;

    /**
     * 更新时间（自动填充）
     */
    private LocalDateTime updateTime;
}