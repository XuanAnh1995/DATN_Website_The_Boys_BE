package backend.datn.mapper;

import backend.datn.dto.response.CustomerResponse;
import backend.datn.entities.Customer;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CustomerMapper {
    public static CustomerResponse toCustomerResponse(Customer customer) {
        if (customer == null) return null;
        return CustomerResponse.builder()
                .id(customer.getId())
                .customerCode(customer.getCustomerCode())
                .fullname(customer.getFullname())
                .username(customer.getUsername() != null ? customer.getUsername() : "N/A") // Tránh lỗi null
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .createDate(customer.getCreateDate() != null ? customer.getCreateDate() : Instant.now()) // Nếu null, đặt thời gian hiện tại
                .updateDate(customer.getUpdateDate() != null ? customer.getUpdateDate() : Instant.now())
                .forgetPassword(customer.getForgetPassword() != null ? customer.getForgetPassword() : false)
                .status(customer.getStatus() != null ? customer.getStatus() : true)
                .build();
    }
}

