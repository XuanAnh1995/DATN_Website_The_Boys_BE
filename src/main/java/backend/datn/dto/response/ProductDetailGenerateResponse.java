package backend.datn.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductDetailGenerateResponse {
    private Integer productId;
    private String productName;

    private Integer size;
    private String sizeName;

    private String brandName;

    private Integer color;
    private String colorName;

    private Integer promotion;
    private String promotionName;

    private Integer collar;
    private String collarName;

    private Integer sleeve;
    private String sleeveName;

    private Integer quantity = 10;

    private BigDecimal salePrice = BigDecimal.valueOf(50000);
    private BigDecimal importPrice = BigDecimal.valueOf(50000);

    private String photo = null;
}

