package backend.datn.dto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderStatusDistributionResponse {

    private Integer statusOrder; // Trạng thái đơn hàng

    private String statusName; // Tên trạng thái

    private Integer orderCount; // Số lượng đơn hàng

}
