package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderDetailUpdateRequest extends OrderDetailCreateRequest {

    @NotNull(message = "ID chi tiết đơn hàng không được để trống")
    private Integer id;

}
