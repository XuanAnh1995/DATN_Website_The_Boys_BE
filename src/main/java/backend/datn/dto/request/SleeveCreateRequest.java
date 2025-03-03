package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SleeveCreateRequest implements Serializable {
    @NotNull(message = "Vui lòng điền thông tin tên tay áo")
    @Size(min = 30, max = 60)
    String sleeveName;
}