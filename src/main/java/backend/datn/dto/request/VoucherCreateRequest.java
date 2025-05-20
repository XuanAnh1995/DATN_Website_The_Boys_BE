package backend.datn.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherCreateRequest implements Serializable {
    @NotNull
    @Size(max = 50)
    String voucherCode;

    @NotNull
    @Size(max = 250)
    String voucherName;

    @Size(max = 255)
    String description;

    @NotNull
    BigDecimal minCondition;

    @NotNull
    BigDecimal maxDiscount;

    @NotNull
    Double reducedPercent;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime startDate;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime endDate;

    @NotNull
    Boolean status;
}