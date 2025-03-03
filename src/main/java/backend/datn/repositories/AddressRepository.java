package backend.datn.repositories;

import backend.datn.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    @Query("Select a from Address a where a.customer.id = :customerId")
    List<Address> findByCustomerId(@Param("customerId") String customerId   );
}