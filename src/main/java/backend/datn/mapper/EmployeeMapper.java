package backend.datn.mapper;

import backend.datn.dto.response.EmployeeResponse;
import backend.datn.entities.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public static EmployeeResponse toEmployeeResponse(Employee employee) {
        if (employee == null) return null;
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .role(employee.getRole() != null ? RoleMapper.toRoleResponse(employee.getRole()) : null)
                .fullname(employee.getFullname())
                .username(employee.getUsername())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .address(employee.getAddress())
                .photo(employee.getPhoto())
                .status(employee.getStatus() != null ? employee.getStatus() : 0)
                .createDate(employee.getCreateDate())
                .updateDate(employee.getUpdateDate())
                .forgetPassword(employee.getForgetPassword())
                .gender(employee.getGender())
                .build();
    }
}
