package SHmarketplace.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 下单请求 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "商品ID不能为空")
    private Integer goodsId;

    @NotNull(message = "成交价格不能为空")
    @DecimalMin(value = "0.01", message = "成交价格不能小于0.01")
    private BigDecimal payPrice;
}
