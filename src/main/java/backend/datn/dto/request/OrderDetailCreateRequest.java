package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailCreateRequest {

    @NotNull
    private Integer orderId;

    @NotNull
    private Integer productDetailId;

    @NotNull
    @Positive
    private Integer quantity;

}
