package backend.datn.dto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class MonthlyRevenueResponse {

    private Integer monthNumber;

    private Integer yearNumber;

    private BigDecimal monthlyRevenue;
}
