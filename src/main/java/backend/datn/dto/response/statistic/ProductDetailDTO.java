package backend.datn.dto.response.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ProductDetailDTO {

    private String productDetailName;

    private Integer totalQuantitySold;

    private BigDecimal totalRevenue;

}
