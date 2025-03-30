package backend.datn.repositories;

import backend.datn.entities.Cart;
import backend.datn.entities.Customer;
import backend.datn.entities.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    void deleteByCustomer(Customer customer);

    Optional<Cart> findByCustomerAndProductDetail(Customer customer, ProductDetail productDetail);

    List<Cart> findByCustomer(Customer customer);
}