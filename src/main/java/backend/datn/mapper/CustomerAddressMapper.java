package backend.datn.mapper;

import backend.datn.dto.response.CustomerAddressResponse;
import backend.datn.entities.Address;

public class CustomerAddressMapper {
    public static CustomerAddressResponse toAddressResponse(Address address) {
        return CustomerAddressResponse.builder()
                .id(address.getId())
                .provinceId(address.getProvinceId())
                .provinceName(address.getProvinceName())
                .districtId(address.getDistrictId())
                .districtName(address.getDistrictName())
                .wardId(address.getWardId())
                .wardName(address.getWardName())
                .addressDetail(address.getAddressDetail())
                .build();
    }
}
