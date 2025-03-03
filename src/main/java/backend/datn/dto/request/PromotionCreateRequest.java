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
public class PromotionCreateRequest implements Serializable {
    Integer id;
    @NotNull
    @Size(max = 255)
    String promotionName;
    @NotNull
    Integer promotionPercent;
    @NotNull
    Instant startDate;
    @NotNull
    Instant endDate;
    @NotNull
    @Size(max = 500)
    String description;
    @NotNull
    Boolean status;
}