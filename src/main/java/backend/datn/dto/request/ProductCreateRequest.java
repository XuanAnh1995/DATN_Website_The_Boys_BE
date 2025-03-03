package backend.datn.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductCreateRequest  {
    @NotNull(message = "Thương hiệu không được để trống")
    private Integer brandId;

    @NotNull(message = "Danh mục không được để trống")
    private Integer categoryId;

    @NotNull(message = "Chất liệu không được để trống")
    private Integer materialId;

    @NotNull(message = "Tên sản phẩm không được để trống")
    private Integer productNameId;
}