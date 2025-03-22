package backend.datn.dto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TopCustomerResponse {

    private String customerName;

    private Integer totalOrders;

    private BigDecimal totalSpent;

}
