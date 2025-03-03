package backend.datn.mapper;

import backend.datn.dto.response.EmployeeResponse;
import backend.datn.entities.Employee;


public class EmployeeMapper {
    public static EmployeeResponse toEmployeeResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .role(RoleMapper.toRoleResponse(employee.getRole()))
                .fullname(employee.getFullname())
                .username(employee.getUsername())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .address(employee.getAddress())
                .photo(employee.getPhoto())
                .status(employee.getStatus())
                .createDate(employee.getCreateDate())
                .updateDate(employee.getUpdateDate())
                .forgetPassword(employee.getForgetPassword())
                .gender(employee.getGender())
                .build();
    }
}
