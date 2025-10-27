package backend.datn.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "brand")
@ToString(exclude = "listProducts")
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)  // dành cho GENERATE TABLE/CỘT
    private Long id;

    @NotBlank(message = "Tên thương hiệu không được để trống")
    @Size(min = 5, max = 100, message = "Tên thương hiệu phải có độ dài từ 5 đến 100 kí tự")
    // validation từ form nhập đưa lên
    @Nationalized
    @Column(name = "brand_name", nullable = false)
    private String brandName;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @OneToMany(
            mappedBy = "brand",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Product> listProducts = new ArrayList<Product>();

    /**
     * Lấy danh sách sản phẩm của thương hiệu.
     *
     * @param onlyActive Nếu true, chỉ trả về các sản phẩm đang hoạt động (status = true).
     * @return Danh sách sản phẩm.
     */
    public List<Product> getAllProductsInBrand(boolean onlyActive) {
        if (onlyActive) {
            return listProducts.stream()
                    .filter(Product::getStatus)
                    .collect(Collectors.toList());
        }
        return listProducts;
    }

    /**
     * Thêm một sản phẩm vào danh sách sản phẩm của thương hiệu và thiết lập thương hiệu cho sản phẩm.
     *
     * @param product Sản phẩm cần thêm.
     * @throws IllegalArgumentException nếu sản phẩm là null.
     */
    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Sản phẩm không được null");
        }
        listProducts.add(product);
        product.setBrand(this);
    }

    /**
     * Xóa một sản phẩm khỏi danh sách sản phẩm của thương hiệu và hủy liên kết thương hiệu của sản phẩm.
     *
     * @param product Sản phẩm cần xóa.
     * @throws IllegalArgumentException nếu sản phẩm là null.
     */
    public void removeProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Sản phẩm không được null");
        }
        listProducts.remove(product);
        product.setBrand(null);
    }
}