package backend.datn.repositories;

import backend.datn.entities.OrderPOS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderPOSRepository extends JpaRepository<OrderPOS, Integer> {

    // Tìm đơn hàng POS theo mã
    OrderPOS findByOrderCode(String orderCode);

    // Phương thức đảm bảo chỉ lấy đơn hàng POS (kindOfOrder = true)
    Optional<OrderPOS> findByOrderCodeAndKindOfOrder(String orderCode, Boolean kindOfOrder);

    // Lấy tất cả đơn hàng POS với tìm kiếm, phân trang và JOIN với Customer
    @Query("SELECT o FROM OrderPOS o " +
            "JOIN o.customer c " +
            "LEFT JOIN o.employee e " +
            "LEFT JOIN o.voucher v " +
            "WHERE o.kindOfOrder = :kindOfOrder " +
            "AND (:search IS NULL OR " +
            "LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.fullname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.phone) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.fullname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(v.voucherCode) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<OrderPOS> findAllByKindOfOrderWithSearchAndJoin(
            @Param("kindOfOrder") Boolean kindOfOrder,
            @Param("search") String search,
            Pageable pageable);

    // Tìm đơn hàng POS theo ID với kiểm tra kindOfOrder = true
    @Query("SELECT o FROM OrderPOS o WHERE o.id = :id AND o.kindOfOrder = :kindOfOrder")
    Optional<OrderPOS> findOrderPOSByIdWithKindOfOrder(
            @Param("id") Integer id,
            @Param("kindOfOrder") Boolean kindOfOrder);

}
