package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductCreateRequest  {
    @NotNull(message = "Thương hiệu không được để trống")
    private Long brandId;

    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;

    @NotNull(message = "Chất liệu không được để trống")
    private Long materialId;

    @NotNull(message = "Tên sản phẩm không được để trống")
    private String productName;
}
