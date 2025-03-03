package backend.datn.repositories;

import backend.datn.entities.Sleeve;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SleeveRepository extends JpaRepository<Sleeve, Integer> {

    @Query(value = """
            SELECT * FROM sleeve  
            WHERE :search IS NULL OR LOWER(sleeve_name) LIKE LOWER(CONCAT('%', :search, '%'))
            """, nativeQuery = true)
    Page<Sleeve> searchSleeve(@Param("search") String search, Pageable pageable);

    boolean existsBySleeveName(String sleeveName);
}