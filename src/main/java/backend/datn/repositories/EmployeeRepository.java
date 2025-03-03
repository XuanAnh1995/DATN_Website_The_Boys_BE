package backend.datn.repositories;

import backend.datn.entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    @Query("SELECT c FROM Employee c " +
            "WHERE (:keyword IS NULL OR :keyword = '' " +
            "OR c.fullname LIKE %:keyword% " +
            "OR c.username LIKE %:keyword% " +
            "OR c.email LIKE %:keyword% " +
            "OR c.phone LIKE %:keyword%)")
    Page<Employee> searchEmployees(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    @Query("SELECT COUNT(c) > 0 FROM Employee c WHERE c.username = :username AND c.id <> :id")
    boolean existsByUsernameAndNotId(@Param("username") String username, @Param("id") Integer id);

    @Query("SELECT COUNT(c) > 0 FROM Employee c WHERE c.email = :email AND c.id <> :id")
    boolean existsByEmailAndNotId(@Param("email") String email, @Param("id") Integer id);

    @Query("SELECT COUNT(c) > 0 FROM Employee c WHERE c.phone = :phone AND c.id <> :id")
    boolean existsByPhoneAndNotId(@Param("phone") String phone, @Param("id") Integer id);

    Employee findByUsername(String username);
    Employee findByEmail(String email);
    Employee findByPhone(String phone);
}