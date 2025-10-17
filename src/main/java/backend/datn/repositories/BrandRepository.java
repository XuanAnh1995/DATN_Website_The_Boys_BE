package backend.datn.repositories;

import backend.datn.entities.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    @Query("""
                SELECT b FROM Brand b
                WHERE (:search IS NULL OR :search = '' OR LOWER(b.brandName) LIKE LOWER('%' || :search || '%'))
            """)
    Page<Brand> searchBrand(@Param("search") String search, Pageable pageable);


    boolean existsByBrandName(String brandName);
}