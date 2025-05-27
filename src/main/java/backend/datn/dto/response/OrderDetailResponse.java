package backend.datn.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponse implements Serializable {

    private Integer id;

    @JsonIgnoreProperties("orderDetails")
    private OrderResponse order;

    private ProductDetailResponse productDetail;

    private BigDecimal price;

    private Integer quantity;

}