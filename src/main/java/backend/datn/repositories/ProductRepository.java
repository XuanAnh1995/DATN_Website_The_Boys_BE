package backend.datn.repositories;

import backend.datn.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE ( p.productName LIKE %:keyword%"
            + " OR p.category.categoryName LIKE %:keyword%"
            + " OR p.material.materialName LIKE %:keyword%"
            + " OR p.brand.brandName LIKE %:keyword% "
            + " OR :keyword is NULL) "
            + " AND :status is NULL OR p.status = :status ")
    Page<Product> findAllWithFilters(String keyword, Boolean status, Pageable pageable);
}