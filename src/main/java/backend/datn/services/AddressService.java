package backend.datn.services;

import backend.datn.dto.request.AddressCreateRequest;
import backend.datn.dto.response.AddressResponse;
import backend.datn.entities.Address;
import backend.datn.entities.Customer;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.mapper.AddressMapper;
import backend.datn.repositories.AddressRepository;
import backend.datn.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public AddressResponse getById(Integer id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Địa chỉ không tồn tại"));
        return AddressMapper.toAddressResponse(address);
    }

    public List<AddressResponse> getByCustomerId(String customerId) {
        List<Address> addresses = addressRepository.findByCustomerId(customerId);
        return addresses.stream()
                .map(AddressMapper::toAddressResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse create(AddressCreateRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Khách hàng không tồn tại"));

        Address address = new Address();
        address.setCustomer(customer);
        address.setProvinceId(request.getProvinceId());
        address.setProvinceName(request.getProvinceName());
        address.setDistrictId(request.getDistrictId());
        address.setDistrictName(request.getDistrictName());
        address.setWardId(request.getWardId());
        address.setWardName(request.getWardName());
        address.setAddressDetail(request.getAddressDetail());
        address = addressRepository.save(address);
        return AddressMapper.toAddressResponse(address);
    }

    @Transactional
    public AddressResponse update(Integer id, AddressCreateRequest request) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Địa chỉ không tồn tại"));

        address.setProvinceId(request.getProvinceId());
        address.setProvinceName(request.getProvinceName());
        address.setDistrictId(request.getDistrictId());
        address.setDistrictName(request.getDistrictName());
        address.setWardId(request.getWardId());
        address.setWardName(request.getWardName());
        address.setAddressDetail(request.getAddressDetail());

        address = addressRepository.save(address);
        return AddressMapper.toAddressResponse(address);
    }

    @Transactional
    public void delete(Integer id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Địa chỉ không tồn tại"));
        addressRepository.delete(address);
    }
}
