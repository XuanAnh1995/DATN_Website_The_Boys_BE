package backend.datn.repositories;

import backend.datn.entities.Collar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CollarRepository extends JpaRepository<Collar, Integer> {
    @Query("SELECT c FROM Collar c WHERE :search IS NULL OR  LOWER(c.collarName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Collar> searchCollars(@Param("search") String search, Pageable pageable);
    boolean existsByCollarName(String name);
}