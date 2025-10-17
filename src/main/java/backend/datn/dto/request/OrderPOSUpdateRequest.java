package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class OrderPOSUpdateRequest extends OrderPOSCreateRequest {

    @NotNull(message = "ID đơn hàng không được để trống")
    private Long id;

}
