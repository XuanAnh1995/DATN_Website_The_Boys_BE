package backend.datn.security;

import backend.datn.entities.Customer;
import backend.datn.entities.Employee;
import backend.datn.repositories.CustomerRepository;
import backend.datn.repositories.EmployeeRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private  EmployeeRepository employeeRepository;

    @Autowired
    private  CustomerRepository customerRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Kiểm tra Employee trước
        Employee employee = employeeRepository.findByUsername(username);
        if (employee != null) {
            Hibernate.initialize(employee.getRole());
            return new CustomUserDetails(employee);
        }

        // Nếu không phải Employee, kiểm tra Customer
        Customer customer = customerRepository.findByUsername(username);
        if (customer != null) {
            return new CustomUserDetails(customer);
        }

        throw new UsernameNotFoundException("User not found");
    }
}
