package backend.datn.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class VoucherResponse {
    Integer id;
    String voucherCode;
    String voucherName;
    String description;
    BigDecimal minCondition;
    BigDecimal maxDiscount;
    Double reducedPercent;
    Instant startDate;
    Instant endDate;
    Boolean status;
}