package backend.datn.services;

import backend.datn.dto.request.CustomerCreateRequest;
import backend.datn.dto.request.CustomerUpdateRequest;
import backend.datn.dto.response.CustomerResponse;
import backend.datn.entities.Customer;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.helpers.CodeGeneratorHelper;
import backend.datn.helpers.RandomHelper;
import backend.datn.mapper.CustomerMapper;
import backend.datn.repositories.CustomerRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    public Page<CustomerResponse> getAllCustomers(String search, int page, int size, String sortBy, String sortDir) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "id";
        }

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Customer> customers = customerRepository.searchCustomers(search, pageable);

        return customers.map(CustomerMapper::toCustomerResponse);
    }

    public CustomerResponse getCustomerById(int id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
        return CustomerMapper.toCustomerResponse(customer);
    }

    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        if (customerRepository.existsByUsername(request.getUsername())) {
            throw new EntityAlreadyExistsException("Tên đăng nhập đã tồn tại.");
        }
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new EntityAlreadyExistsException("Email đã tồn tại.");
        }
        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new EntityAlreadyExistsException("Số điện thoại đã tồn tại.");
        }

        Customer customer = new Customer();
        customer.setCustomerCode(CodeGeneratorHelper.generateCode("USR"));
        customer.setFullname(request.getFullname());
        customer.setUsername(request.getUsername());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        customer.setCreateDate(Instant.now());
        customer.setForgetPassword(false);
        customer.setStatus(true);

        String rawPassword = RandomHelper.generateRandomString(8);
        String hashedPassword = passwordEncoder.encode(rawPassword);
        customer.setPassword(hashedPassword);

        customer = customerRepository.save(customer);

        mailService.sendNewPasswordMail(customer.getUsername(), customer.getEmail(), rawPassword);

        return CustomerMapper.toCustomerResponse(customer);
    }

    public CustomerResponse updateCustomer(int id, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng với ID: " + id));

        if (customerRepository.existsByEmailAndNotId(request.getEmail(), id)) {
            throw new EntityAlreadyExistsException("Email đã tồn tại.");
        }
        if (customerRepository.existsByPhoneAndNotId(request.getPhone(), id)) {
            throw new EntityAlreadyExistsException("Số điện thoại đã tồn tại.");
        }

        customer.setFullname(request.getFullname());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setUpdateDate(Instant.now());

        customer = customerRepository.save(customer);

        return CustomerMapper.toCustomerResponse(customer);
    }

    public CustomerResponse toggleStatusCustomer(int id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng với ID: " + id));

        customer.setStatus(!customer.getStatus());
        customer = customerRepository.save(customer);
        return CustomerMapper.toCustomerResponse(customer);
    }

    public Optional<Customer> findById(@NotNull Integer customerId) {
        return customerRepository.findById(customerId);
    }
}
