package backend.datn.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long id;
    private Long productDetailId;
    private String productName;
    private String productDetailName;
    private String photo;
    private String brandName;
    private String categoryName;

    private Integer quantity;
    private BigDecimal price;
    private BigDecimal discountPrice;
}