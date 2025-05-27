package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailCreateRequest {

    @NotNull(message = "ID đơn hàng không được để trống")
    private Integer orderId;

    @NotNull(message = "ID chi tiết sản phẩm không được để trống")
    private Integer productDetailId;

    @NotNull(message = "Số lượng không được để trống")
    @Positive(message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    private BigDecimal price;

}
