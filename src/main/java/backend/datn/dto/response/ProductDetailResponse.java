package backend.datn.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link backend.datn_hn37.entities.ProductDetail}
 */
@Data
@Builder
public class ProductDetailResponse implements Serializable {
    private final Integer id;
    private final ProductResponse product;
    private final SizeResponse size;
    private final ColorResponse color;
    private final PromotionResponse promotion;
    private final CollarResponse collar;
    private final SleeveResponse sleeve;
    private final String photo;
    private final String productDetailCode;
    private final BigDecimal importPrice;
    private final BigDecimal salePrice;
    private final Integer quantity;
    private final String description;
    private final Boolean status;
}