package backend.datn.repositories;

import backend.datn.entities.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    @Query("""
            SELECT v FROM Voucher v 
            WHERE (:search IS NULL OR :search = '' 
                   OR LOWER(v.voucherName) LIKE '%' || LOWER(:search) || '%' 
                   OR LOWER(v.voucherCode) LIKE '%' || LOWER(:search) || '%')
            AND (:status IS NULL OR v.status = :status)
            AND (:minCondition IS NULL OR v.minCondition >= :minCondition)
            AND (:reducedPercent IS NULL OR v.reducedPercent >= :reducedPercent)
            """)
    Page<Voucher> searchVouchers(
            @Param("search") String search,
            @Param("status") Boolean status,
            @Param("minCondition") BigDecimal minCondition,
            @Param("reducedPercent") Double reducedPercent,
            Pageable pageable);

    boolean existsByVoucherCode(String voucherCode);
}