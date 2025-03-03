package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CollarCreateRequest {
    @NotNull(message = "Vui lòng điền thông tin tên tay áo")
    String name;
}