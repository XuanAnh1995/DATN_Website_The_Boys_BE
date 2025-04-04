package backend.datn.repositories;

import backend.datn.dto.response.ProductDetailResponse;
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
            "WHERE (:search IS NULL OR pd.product.productName LIKE %:search% OR pd.productDetailCode LIKE  :search OR pd.product.productCode LIKE :search  ) " +
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

    @Query("SELECT pd FROM ProductDetail pd WHERE pd.product.productCode = :productCode")
    List<ProductDetail> findByProductCode(@Param("productCode") String productCode);
    // Phương thức cập nhật: Lấy các biến thể của một sản phẩm dưới dạng ProductDetailResponse
    @Query("""
    SELECT new backend.datn.dto.response.ProductDetailResponse(
        pd.id,
        new backend.datn.dto.response.ProductResponse(
            p.id,
            new backend.datn.dto.response.BrandResponse(p.brand.id, p.brand.brandName, p.brand.status),
            new backend.datn.dto.response.CategoryResponse(p.category.id, p.category.categoryName, p.category.status),
            new backend.datn.dto.response.MaterialResponse(p.material.id, p.material.materialName, p.material.status),
            p.productName,
            p.productCode,
            p.status
        ),
        new backend.datn.dto.response.SizeResponse(s.id, s.sizeName, s.status),
        new backend.datn.dto.response.ColorResponse(c.id, c.colorName, c.status),
        new backend.datn.dto.response.PromotionResponse(pr.id, pr.promotionName, pr.promotionPercent, pr.startDate, pr.endDate, pr.description, pr.status),
        new backend.datn.dto.response.CollarResponse(col.id, col.collarName, col.status),
        new backend.datn.dto.response.SleeveResponse(sl.id, sl.sleeveName, sl.status),
        pd.photo,
        pd.productDetailCode,
        pd.importPrice,
        pd.salePrice,
        pd.quantity,
        pd.description,
        pd.status
    )
    FROM ProductDetail pd
    LEFT JOIN Product p ON pd.product.id = p.id
    LEFT JOIN Color c ON pd.color.id = c.id
    LEFT JOIN Size s ON pd.size.id = s.id
    LEFT JOIN Collar col ON pd.collar.id = col.id
    LEFT JOIN Sleeve sl ON pd.sleeve.id = sl.id
    LEFT JOIN Material m ON p.material.id = m.id
    LEFT JOIN Promotion pr ON pd.promotion.id = pr.id
    WHERE p.productCode = :productCode
""")
    List<ProductDetailResponse> getProductVariantsByProductCode(@Param("productCode") String productCode);
}