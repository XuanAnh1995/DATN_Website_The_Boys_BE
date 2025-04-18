package backend.datn.repositories;

import backend.datn.entities.Order;
import backend.datn.entities.OrderDetail;
import backend.datn.entities.OrderOnlineDetail;
import backend.datn.entities.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderOnlineDetailRepository extends JpaRepository<OrderOnlineDetail, Integer> {

}