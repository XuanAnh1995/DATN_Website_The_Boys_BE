package backend.datn.repositories;

import backend.datn.dto.response.UserProductResponse;
import backend.datn.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE ( p.productName LIKE %:keyword%"
            + " OR p.category.categoryName LIKE %:keyword%"
            + " OR p.material.materialName LIKE %:keyword%"
            + " OR p.brand.brandName LIKE %:keyword% "
            + " OR :keyword is NULL) "
            + " AND :status is NULL OR p.status = :status ")
    Page<Product> findAllWithFilters(String keyword, Boolean status, Pageable pageable);

    @Query("""
    SELECT new backend.datn.dto.response.UserProductResponse(
        p.id,
        p.productCode,
        p.productName,
        COALESCE(SUM(pd.quantity), 0), 
        COALESCE(SUM(CASE WHEN o.statusOrder = 5 THEN od.quantity ELSE 0 END), 0),
        (SELECT pd2.photo FROM ProductDetail pd2 WHERE pd2.product.id = p.id ORDER BY pd2.id ASC FETCH FIRST 1 ROWS ONLY),
        (SELECT MIN(pd3.salePrice) FROM ProductDetail pd3 WHERE pd3.product.id = p.id),
        (SELECT pd4.description FROM ProductDetail pd4 WHERE pd4.product.id = p.id ORDER BY pd4.id ASC FETCH FIRST 1 ROWS ONLY)
    )
    FROM Product p
    JOIN ProductDetail pd ON pd.product.id = p.id
    LEFT JOIN OrderDetail od ON od.productDetail.id = pd.id
    LEFT JOIN Order o ON od.order.id = o.id AND o.statusOrder = 5
    WHERE (:search IS NULL OR p.productName LIKE %:search%)
    AND (:brandIds IS NULL OR p.brand.id IN :brandIds)
    AND (:categoryIds IS NULL OR p.category.id IN :categoryIds)
    AND (:materialIds IS NULL OR p.material.id IN :materialIds)
    AND (:collarIds IS NULL OR pd.collar.id IN :collarIds)
    AND (:sleeveIds IS NULL OR pd.sleeve.id IN :sleeveIds)
    AND (:colorIds IS NULL OR pd.color.id IN :colorIds)
    AND (:sizeIds IS NULL OR pd.size.id IN :sizeIds)
    AND (:minPrice IS NULL OR pd.salePrice >= :minPrice)
    AND (:maxPrice IS NULL OR pd.salePrice <= :maxPrice)
    GROUP BY p.id, p.productCode, p.productName
""")
    Page<UserProductResponse> findAllWithFilters(
            @Param("search") String search,
            @Param("brandIds") List<Integer> brandIds,
            @Param("categoryIds") List<Integer> categoryIds,
            @Param("materialIds") List<Integer> materialIds,
            @Param("collarIds") List<Integer> collarIds,
            @Param("sleeveIds") List<Integer> sleeveIds,
            @Param("colorIds") List<Integer> colorIds,
            @Param("sizeIds") List<Integer> sizeIds,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );



}