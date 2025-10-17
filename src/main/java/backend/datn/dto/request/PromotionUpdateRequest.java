package backend.datn.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
public class PromotionUpdateRequest implements Serializable {

    Long id;

    @NotNull(message = "Không đẻ trống tên")
    @Size(max = 255)
    String promotionName;

    @NotNull(message = "Không để trống ")
    Integer promotionPercent;

    @NotNull(message = "Không để trống ngày bắt đầu")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime startDate;

    @NotNull(message = "Không để trống ngày kết thúc")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime endDate;

    @NotNull(message = "Không để trống mô tả")
    @Size(max = 500)
    String description;

    Boolean status;
}