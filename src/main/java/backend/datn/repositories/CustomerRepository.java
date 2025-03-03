package backend.datn.repositories;

import backend.datn.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("SELECT c FROM Customer c " +
            "WHERE (:keyword IS NULL OR :keyword = '' " +
            "OR c.fullname LIKE %:keyword% " +
            "OR c.username LIKE %:keyword% " +
            "OR c.email LIKE %:keyword% " +
            "OR c.phone LIKE %:keyword%)")
    Page<Customer> searchCustomers(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    Optional<Customer> findById(Integer id);

    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.username = :username AND c.id <> :id")
    boolean existsByUsernameAndNotId(@Param("username") String username, @Param("id") Integer id);

    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.email = :email AND c.id <> :id")
    boolean existsByEmailAndNotId(@Param("email") String email, @Param("id") Integer id);

    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.phone = :phone AND c.id <> :id")
    boolean existsByPhoneAndNotId(@Param("phone") String phone, @Param("id") Integer id);

    Customer findByUsername(String username);
    Customer findByEmail(String email);
    Customer findByPhone(String phone);

}