package backend.datn.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;


@Data
@Builder
public class PromotionResponse implements Serializable {

    private final Integer id;

    private final String promotionName;

    private final Integer promotionPercent;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private final Instant startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private final Instant endDate;

    private final String description;

    private final Boolean status;

}