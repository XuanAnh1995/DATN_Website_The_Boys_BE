package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandUpdateRequest implements Serializable {
    @NotNull
    Integer id;

    @NotNull(message = "Vui lòng điền thông tin tên nhãn hàng")
    String brandName;
}