package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
public class PromotionUpdateRequest implements Serializable {
    Integer id;
    @NotNull(message = "Không đẻ trống tên")
    @Size(max = 255)
    String promotionName;
    @NotNull(message = "Không để trống ")
    Integer promotionPercent;
    @NotNull(message = "Không để trống ngày bắt đầu")
    Instant startDate;
    @NotNull(message = "Không để trống ngày kết thúc")
    Instant endDate;
    @NotNull(message = "Không để trống mô tả")
    @Size(max = 500)
    String description;
    Boolean status;
}