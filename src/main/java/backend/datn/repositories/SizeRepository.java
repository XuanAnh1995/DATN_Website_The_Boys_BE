package backend.datn.repositories;


import backend.datn.entities.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SizeRepository extends JpaRepository<Size, Integer> {
    @Query("SELECT c FROM Size c WHERE :search IS NULL OR  LOWER(c.sizeName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Size> searchSizes(@Param("search") String search, Pageable pageable);
    boolean existsBySizeName(String name);
}