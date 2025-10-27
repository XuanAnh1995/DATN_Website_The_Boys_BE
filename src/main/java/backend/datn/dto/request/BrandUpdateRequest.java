package backend.datn.dto.request;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Tên thương hiệu không được để trống")
    @Size(min = 5, max = 100, message = "Tên thương hiệu phải có độ dài từ 5 đến 100 kí tự")
    String brandName;
}