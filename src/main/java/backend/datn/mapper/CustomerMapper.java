package backend.datn.mapper;

import backend.datn.dto.response.CustomerResponse;
import backend.datn.entities.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public static CustomerResponse toCustomerResponse(Customer customer) {
        if (customer == null) return null;
        return CustomerResponse.builder()
                .id(customer.getId())
                .customerCode(customer.getCustomerCode())
                .fullname(customer.getFullname())
                .username(customer.getUsername())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .createDate(customer.getCreateDate())
                .updateDate(customer.getUpdateDate())
                .forgetPassword(customer.getForgetPassword())
                .status(customer.getStatus())
                .build();
    }
}
