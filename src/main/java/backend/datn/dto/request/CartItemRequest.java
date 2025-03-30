package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequest {
    @NotNull(message = "Product detail ID không được để trống")
    private Integer productDetailId;

    @NotNull(message = "Số lượng không được để trống")
    private Integer quantity;
}