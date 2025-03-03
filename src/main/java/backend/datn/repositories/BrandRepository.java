package backend.datn.repositories;

import backend.datn.entities.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer> {

    @Query(value = """
                SELECT * FROM brand 
                WHERE COALESCE(:search, '') = '' OR LOWER(brand_name) LIKE LOWER(CONCAT('%', :search, '%'))
            """, nativeQuery = true)
    Page<Brand> searchBrand(@Param("search") String search, Pageable pageable);


    boolean existsByBrandName(String brandName);
}