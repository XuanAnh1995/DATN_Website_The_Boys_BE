package backend.datn.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link backend.datn.entities.OrderDetail}
 */
@Data
@Builder
public class OrderDetailResponse implements Serializable {

    private Integer id;

    private OrderResponse order;

    private ProductDetailResponse productDetail;

    private Integer quantity;

}