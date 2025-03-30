package backend.datn.repositories;


import backend.datn.entities.OrderOnline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderOnlineRepository extends JpaRepository<OrderOnline, Integer> {

    // Tìm đơn hàng theo mã
    OrderOnline findByOrderCode(String orderCode);


}

