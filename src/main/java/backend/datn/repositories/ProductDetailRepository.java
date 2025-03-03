package backend.datn.repositories;

import backend.datn.entities.ProductDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer> {

    @Query("SELECT CASE WHEN COUNT(pd) > 0 THEN true ELSE false END FROM ProductDetail pd " +
            "WHERE pd.product.id = :productId AND pd.size.id = :size AND pd.color.id = :color " +
            "AND pd.collar.id = :collar AND pd.sleeve.id = :sleeve")
    boolean existsByProductAndSizeAndColorAndCollarAndSleeve(@Param("productId") Integer productId,
                                                             @Param("size") Integer size,
                                                             @Param("color") Integer color,
                                                             @Param("collar") Integer collar,
                                                             @Param("sleeve") Integer sleeve);

    @Query("SELECT pd FROM ProductDetail pd " +
            "WHERE (:search IS NULL OR pd.product.productName LIKE %:search% OR pd.productDetailCode LIKE  %:search% ) " +
            "AND (:sizeIds IS NULL OR pd.size.id IN :sizeIds) " +
            "AND (:colorIds IS NULL OR pd.color.id IN :colorIds) " +
            "AND (:collarIds IS NULL OR pd.collar.id IN :collarIds) " +
            "AND (:sleeveIds IS NULL OR pd.sleeve.id IN :sleeveIds) " +
            "AND (:minPrice IS NULL OR pd.salePrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR pd.salePrice <= :maxPrice)")
    Page<ProductDetail> findBySearchAndFilter(@Param("search") String search,
                                              @Param("sizeIds") List<Integer> sizeIds,
                                              @Param("colorIds") List<Integer> colorIds,
                                              @Param("collarIds") List<Integer> collarIds,
                                              @Param("sleeveIds") List<Integer> sleeveIds,
                                              @Param("minPrice") Double minPrice,
                                              @Param("maxPrice") Double maxPrice,
                                              Pageable pageable);

    // Tìm sản phẩm theo product_id
    List<ProductDetail> findByProductId(int productId);

    // Tìm sản phẩm theo màu sắc
    List<ProductDetail> findByColorId(int colorId);

    // Tìm sản phẩm theo size
    List<ProductDetail> findBySizeId(int sizeId);

    // Tìm sản phẩm theo mã sản phẩm
    ProductDetail findByProductDetailCode(String productDetailCode);

}