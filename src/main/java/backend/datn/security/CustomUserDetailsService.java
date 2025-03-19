package backend.datn.security;

import backend.datn.entities.Employee;
import backend.datn.repositories.EmployeeRepository;
import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByUsername(username);
        if (employee == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Ép Hibernate load role trước khi session đóng
        Hibernate.initialize(employee.getRole());

        return new CustomUserDetails(employee);
    }
}
