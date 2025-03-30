package backend.datn.dto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodDistributionResponse {

    private Integer paymentMethod;

    private String methodName;

    private Integer orderCount;

}
