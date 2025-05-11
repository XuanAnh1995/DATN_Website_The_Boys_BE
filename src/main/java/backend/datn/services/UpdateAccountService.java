package backend.datn.services;

import backend.datn.dto.request.AddressCreateRequest;
import backend.datn.dto.request.CustomerAddressRequest;
import backend.datn.dto.request.UpdateCustomerInfomationRequest;
import backend.datn.dto.request.UpdateCustomerPasswordRequest;
import backend.datn.dto.response.AddressResponse;
import backend.datn.dto.response.CustomerAddressResponse;
import backend.datn.dto.response.CustomerResponse;
import backend.datn.entities.Address;
import backend.datn.entities.Customer;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.mapper.AddressMapper;
import backend.datn.mapper.CustomerAddressMapper;
import backend.datn.mapper.CustomerMapper;
import backend.datn.repositories.AddressRepository;
import backend.datn.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import backend.datn.security.CustomUserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UpdateAccountService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AddressRepository addressRepository;

    private Customer getCurrentCustomer() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof CustomUserDetails)) {
            throw new BadCredentialsException("Người dùng chưa đăng nhập hoặc thông tin xác thực không hợp lệ");
        }

        String username = ((CustomUserDetails) principal).getUsername();
        Customer customer = customerRepository.findByUsername(username);

        if (customer == null) {
            throw new EntityNotFoundException("Không tìm thấy tài khoản khách hàng");
        }

        return customer;
    }

    public CustomerResponse updateCustomerInfo(UpdateCustomerInfomationRequest request) {
        Customer customer = getCurrentCustomer();
        customer.setFullname(request.getFullname());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customerRepository.save(customer);
        return CustomerMapper.toCustomerResponse(customer);
    }

    public void updateCustomerPassword(UpdateCustomerPasswordRequest request) {
        Customer customer = getCurrentCustomer();
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        customer.setPassword(encodedPassword);
        customerRepository.save(customer);
    }
    public List<CustomerAddressResponse> getCurrentCustomerAddresses() {
        Customer customer = getCurrentCustomer();
        List<Address> addresses = addressRepository.findByCustomer(customer);
        return addresses.stream()
                .map(CustomerAddressMapper::toAddressResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerAddressResponse createAddress(CustomerAddressRequest request) {
        Customer customer = getCurrentCustomer();

        Address address = new Address();
        address.setCustomer(customer);
        address.setProvinceId(request.getProvinceId());
        address.setProvinceName(request.getProvinceName());
        address.setDistrictId(request.getDistrictId());
        address.setDistrictName(request.getDistrictName());
        address.setWardId(request.getWardId());
        address.setWardName(request.getWardName());
        address.setAddressDetail(request.getAddressDetail());

        Address saved = addressRepository.save(address);

        return CustomerAddressMapper.toAddressResponse(saved);
    }

    @Transactional
    public void deleteAddress(Integer addressId) {
        Customer customer = getCurrentCustomer();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy địa chỉ"));

        if (!address.getCustomer().getId().equals(customer.getId())) {
            throw new BadCredentialsException("Không có quyền xóa địa chỉ này");
        }

        addressRepository.delete(address);
    }

}
