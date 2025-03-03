package backend.datn.mapper;

import backend.datn.dto.response.CustomerResponse;
import backend.datn.entities.Customer;


public class CustomerMapper {
    public static CustomerResponse toCustomerResponse(Customer customer) {
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
