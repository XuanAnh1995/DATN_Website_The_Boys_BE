package backend.datn.repositories;

import backend.datn.entities.Color;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ColorRepository extends JpaRepository<Color, Integer> {
    @Query("SELECT c FROM Color c WHERE :search IS NULL OR  LOWER(c.colorName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Color> searchColors(@Param("search") String search, Pageable pageable);
    boolean existsByColorName(String name);
}