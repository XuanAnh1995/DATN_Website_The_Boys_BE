package backend.datn.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductDetailGenerateResponse {
    private Long productId;
    private String productName;

    private Long size;
    private String sizeName;

    private String brandName;

    private Long color;
    private String colorName;

    private Long promotion;
    private String promotionName;

    private Long collar;
    private String collarName;

    private Long sleeve;
    private String sleeveName;

    private Integer quantity = 10;

    private BigDecimal salePrice = BigDecimal.valueOf(50000);
    private BigDecimal importPrice = BigDecimal.valueOf(50000);

    private String photo = null;
}

