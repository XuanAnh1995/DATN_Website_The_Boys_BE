package backend.datn.repositories;

import backend.datn.entities.Voucher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Integer> {
    @Query("SELECT v FROM Voucher v WHERE :search IS NULL OR " +
            "LOWER(v.voucherName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(v.voucherCode) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Voucher> searchVouchers(@Param("search") String search, Pageable pageable);

    boolean existsByVoucherCode(String voucherCode);
}