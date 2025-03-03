package backend.datn.repositories;

import backend.datn.entities.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {

    @Query(value = """
            SELECT * FROM material  
            WHERE :search IS NULL OR LOWER(material_name) LIKE LOWER(CONCAT('%', :search, '%'))
            """, nativeQuery = true)
    Page<Material> searchBrand(@Param("search") String search, Pageable pageable);

    boolean existsByMaterialName(String materialName);
}