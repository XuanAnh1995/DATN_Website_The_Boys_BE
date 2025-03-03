package backend.datn.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDetailCreateRequest {
    @NotNull(message = "Sản phẩm không được để trống")
    private Integer productId;

    @NotEmpty(message = "Kích thước không được để trống")
    private List<Integer> sizeId;

    @NotEmpty(message = "Màu sắc không được để trống")
    private List<Integer> colorId;

    @NotEmpty(message = "Cổ áo không được để trống")
    private List<Integer> collarId;

    @NotEmpty(message = "Tay áo không được để trống")
    private List<Integer> sleeveId;

    private Integer promotionId;

    @Size(max = 250, message = "Ảnh không được vượt quá 250 ký tự")
    @NotNull(message = "Ảnh không được để trống")
    private String photo;

    @NotNull(message = "Giá nhập không được để trống")
    private BigDecimal importPrice;

    @NotNull(message = "Giá bán không được để trống")
    private BigDecimal salePrice;

    @NotNull(message = "Số lượng không được để trống")
    private Integer quantity;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    @NotNull(message = "Mô tả không được để trống")
    private String description;
}