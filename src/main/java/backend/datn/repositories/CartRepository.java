package backend.datn.repositories;

import backend.datn.entities.Cart;
import backend.datn.entities.Customer;
import backend.datn.entities.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    void deleteByCustomer(Customer customer);

    Optional<Cart> findByCustomerAndProductDetail(Customer customer, ProductDetail productDetail);

    List<Cart> findByCustomer(Customer customer);

    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.customer.id = :customerId AND c.productDetail.id IN :productDetailIds")
    void deleteByCustomerAndProductDetailIds(@Param("customerId") Integer customerId,
                                             @Param("productDetailIds") List<Integer> productDetailIds);
}
