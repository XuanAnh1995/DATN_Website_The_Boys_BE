package backend.datn.repositories;

import backend.datn.entities.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    @Query("SELECT p FROM Promotion p WHERE " +
            "(:search IS NULL OR LOWER(p.promotionName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:startDate IS NULL OR p.endDate >= :startDate) " + // Khuyến mãi kết thúc sau startDate
            "AND (:endDate IS NULL OR p.startDate <= :endDate) " +   // Khuyến mãi bắt đầu trước endDate
            "AND (:minPercent IS NULL OR p.promotionPercent >= :minPercent) " +
            "AND (:maxPercent IS NULL OR p.promotionPercent <= :maxPercent) " +
            "AND (:status IS NULL OR p.status = :status)")
    Page<Promotion> searchPromotions(@Param("search") String search,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     @Param("minPercent") Integer minPercent,
                                     @Param("maxPercent") Integer maxPercent,
                                     @Param("status") Boolean status,
                                     Pageable pageable);
}