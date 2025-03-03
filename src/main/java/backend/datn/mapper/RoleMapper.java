package backend.datn.mapper;

import backend.datn.dto.response.RoleResponse;
import backend.datn.entities.Role;


public class RoleMapper {
    public static RoleResponse toRoleResponse(Role role) {
        return RoleResponse.builder().
                id(role.getId()).
                name(role.getName()).
                build();
    }
}
