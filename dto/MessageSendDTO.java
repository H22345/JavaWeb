package SHmarketplace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发送留言请求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageSendDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "商品ID不能为空")
    private Integer goodsId;

    @NotNull(message = "接收人ID不能为空")
    private Integer toUserId;

    @NotBlank(message = "留言内容不能为空")
    @Size(max = 500, message = "留言内容不能超过500字符")
    private String content;
}
