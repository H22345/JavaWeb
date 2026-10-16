package SHmarketplace.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 创建商品请求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "商品名称不能为空")
    @Size(max = 120, message = "商品名称不能超过120字符")
    private String goodsName;

    @Size(max = 255, message = "商品副标题不能超过255字符")
    private String goodsTitle;

    /** 售价：不能小于等于0 */
    @NotNull(message = "售价不能为空")
    @DecimalMin(value = "0.01", message = "售价不能小于0.01")
    private BigDecimal price;

    /** 成本价：不能小于0 */
    @NotNull(message = "成本价不能为空")
    @DecimalMin(value = "0.00", message = "成本价不能小于0")
    private BigDecimal costPrice;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能小于0")
    private Integer stock;

    @Size(max = 255, message = "主图地址不能超过255字符")
    private String goodsImg;
}