package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SizeCreateRequest {
    @NotNull(message = "Vui lòng điền thông tin tên kích thước")
    String name;
}