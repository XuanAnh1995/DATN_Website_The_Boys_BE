package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    Long id;

    @NotNull(message = "Vui lòng điền thông tin tên nhãn hàng")
    @Size(max = 100, message = "Tên nhãn hàng không được vượt quá 100 ký tự")
    String brandName;
}