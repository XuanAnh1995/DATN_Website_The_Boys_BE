package backend.datn.dto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ChannelRevenueResponse {

    private Integer dayNumber;

    private Integer monthNumber;

    private Integer yearNumber;

    private BigDecimal onlineRevenue; // Doanh thu online

    private BigDecimal inStoreRevenue; // Doanh thu tại quầy

}
