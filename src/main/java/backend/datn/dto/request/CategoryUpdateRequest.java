package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryUpdateRequest {
    @NotNull
    Integer id;

    @NotNull(message = "Vui lòng điền thông tin tên thể loại")
    String name;
}
