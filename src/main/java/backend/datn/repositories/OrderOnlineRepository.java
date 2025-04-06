package backend.datn.repositories;


import backend.datn.entities.OrderOnline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderOnlineRepository extends JpaRepository<OrderOnline, Integer> {

    // Tìm đơn hàng theo mã
    OrderOnline findByOrderCode(String orderCode);

    // Phương thức mới để đảm bảo chỉ lấy đơn hàng online
    Optional<OrderOnline> findByOrderCodeAndKindOfOrder(String orderCode, Boolean kindOfOrder);

    // Phương thức mới: Lấy tất cả đơn hàng online với tìm kiếm, phân trang và JOIN với Customer
    @Query("SELECT o FROM OrderOnline o " +
            "JOIN o.customer c " +
            "WHERE o.kindOfOrder = :kindOfOrder " +
            "AND (:search IS NULL OR " +
            "LOWER(o.orderCode) LIKE :search OR " +
            "LOWER(o.phone) LIKE :search OR " +
            "LOWER(o.address) LIKE :search OR " +
            "LOWER(c.fullname) LIKE :search OR " +
            "LOWER(c.email) LIKE :search OR " +
            "LOWER(c.phone) LIKE :search)")
    Page<OrderOnline> findAllByKindOfOrderWithSearchAndJoin(
            @Param("kindOfOrder") Boolean kindOfOrder,
            @Param("search") String search,
            Pageable pageable);

    // Thêm phương thức tìm theo ID
    @Query("SELECT o FROM OrderOnline o WHERE o.id = :id AND o.kindOfOrder = :kindOfOrder")
    Optional<OrderOnline> findOrderOnlineByIdWithKindOfOrder(
            @Param("id") Integer id,
            @Param("kindOfOrder") Boolean kindOfOrder);
}

