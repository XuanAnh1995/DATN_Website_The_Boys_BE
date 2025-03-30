package backend.datn.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class OrderOnlineDetailResponse{
    Integer id;
    OrderOnlineResponse order;
    ProductDetailResponse productDetail;
    BigDecimal price;
    Integer quantity;
}