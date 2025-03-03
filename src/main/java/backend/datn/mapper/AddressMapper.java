package backend.datn.mapper;

import backend.datn.dto.response.AddressResponse;
import backend.datn.entities.Address;

public class AddressMapper {
    public static AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .customer(CustomerMapper.toCustomerResponse(address.getCustomer()))
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