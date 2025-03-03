package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

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
    Instant startDate;
    @NotNull
    Instant endDate;
    @NotNull
    Boolean status;
}