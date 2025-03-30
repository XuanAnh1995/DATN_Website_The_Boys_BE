package backend.datn.services;

import backend.datn.dto.request.LoginRequest;
import backend.datn.dto.response.AddressResponse;
import backend.datn.dto.response.LoginResponse;
import backend.datn.entities.Address;
import backend.datn.entities.Customer;
import backend.datn.entities.Employee;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.mapper.AddressMapper;
import backend.datn.mapper.CustomerMapper;
import backend.datn.mapper.EmployeeMapper;
import backend.datn.repositories.AddressRepository;
import backend.datn.repositories.CustomerRepository;
import backend.datn.repositories.EmployeeRepository;
import backend.datn.security.CustomUserDetails;
import backend.datn.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        // Tìm kiếm người dùng trong bảng Customer
        Customer customer = customerRepository.findByUsernameOrEmail(request.getUsername());
        if (customer != null) {
            // Kiểm tra mật khẩu đã mã hóa
            if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
                throw new BadCredentialsException("Invalid username or password");
            }

            Map<String, Object> claims = new HashMap<>();
            claims.put("role", "CUSTOMER");
            claims.put("fullName", customer.getFullname());
            claims.put("email", customer.getEmail());
            claims.put("phone", customer.getPhone());

            String token = jwtUtil.generateToken(customer.getUsername(), claims);
            return new LoginResponse(
                    token, customer.getUsername(), "CUSTOMER",
                    customer.getFullname(), customer.getEmail(),
                    customer.getPhone(), null, null, customer.getStatus()
            );
        }

        // Tìm kiếm người dùng trong bảng Employee
        Employee employee = employeeRepository.findByUsernameOrEmail(request.getUsername());
        if (employee != null) {
            // Kiểm tra mật khẩu đã mã hóa
            if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
                throw new BadCredentialsException("Invalid username or password");
            }

            Map<String, Object> claims = new HashMap<>();
            claims.put("role", employee.getRole().getName());
            claims.put("fullName", employee.getFullname());
            claims.put("email", employee.getEmail());
            claims.put("phone", employee.getPhone());

            String token = jwtUtil.generateToken(employee.getUsername(), claims);
            return new LoginResponse(
                    token, employee.getUsername(), employee.getRole().getName(),
                    employee.getFullname(), employee.getEmail(),
                    employee.getPhone(), employee.getAddress(),
                    employee.getPhoto(), employee.getStatus() == 1
            );
        }

        // Nếu không tìm thấy, ném lỗi xác thực
        throw new BadCredentialsException("Invalid username or password");
    }

    public Map<String, Object> verifyToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new BadCredentialsException("Token is missing");
        }

        try {
            // Lấy username từ token
            String username = jwtUtil.extractUsername(token);

            // Kiểm tra token có hợp lệ không
            if (!jwtUtil.validateToken(token, username)) {
                throw new BadCredentialsException("Invalid or expired token");
            }

            // Lấy toàn bộ claims từ token
            return jwtUtil.extractAllClaims(token);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid token: " + e.getMessage());
        }
    }


    public boolean resetTempAccounts() {
        String newPassword = passwordEncoder.encode("abc123");

        boolean updated = false;

        // Cập nhật mật khẩu cho admin
        Employee admin = employeeRepository.findByUsername("admin");
        if (admin != null) {
            admin.setPassword(newPassword);
            employeeRepository.save(admin);
            updated = true;
        }

        // Cập nhật mật khẩu cho staff
        Employee staff = employeeRepository.findByUsername("staff");
        if (staff != null) {
            staff.setPassword(newPassword);
            employeeRepository.save(staff);
            updated = true;
        }

        // Cập nhật mật khẩu cho user
        Customer user = customerRepository.findByUsername("user");
        if (user != null) {
            user.setPassword(newPassword);
            customerRepository.save(user);
            updated = true;
        }

        return updated;
    }


    public Object getCurrentUserInfo() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("\n\n\nPrincipal : " + principal);

        if (!(principal instanceof CustomUserDetails)) {
            throw new BadCredentialsException("Người dùng chưa đăng nhập hoặc thông tin xác thực không hợp lệ");
        }

        CustomUserDetails userDetails = (CustomUserDetails) principal;
        String username = userDetails.getUsername();

        Customer customer = customerRepository.findByUsername(username);
        if (customer != null) {
            return CustomerMapper.toCustomerResponse(customer);
        }

        Employee employee = employeeRepository.findByUsername(username);
        if (employee != null) {
            return EmployeeMapper.toEmployeeResponse(employee);
        }

        throw new EntityNotFoundException("Không tìm thấy thông tin tài khoản");
    }



    public List<AddressResponse> getCurrentUserAddresses() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("\n\n\nPrincipal : " + principal);

        if (!(principal instanceof CustomUserDetails)) {
            throw new BadCredentialsException("Người dùng chưa đăng nhập hoặc thông tin xác thực không hợp lệ");
        }

        CustomUserDetails userDetails = (CustomUserDetails) principal;

        String username = userDetails.getUsername();
        Customer customer = customerRepository.findByUsername(username);

        List<Address> addresses = addressRepository.findByCustomer(customer);

        if (addresses.isEmpty()) {
            return null;
        }

        return addresses.stream()
                .map(AddressMapper::toAddressResponse)
                .toList();
    }

}
