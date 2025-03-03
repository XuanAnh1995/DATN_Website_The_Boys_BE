package backend.datn.services;

import backend.datn.dto.request.EmployeeCreateRequest;
import backend.datn.dto.request.EmployeeUpdateRequest;
import backend.datn.dto.response.EmployeeResponse;
import backend.datn.entities.Employee;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.helpers.CodeGeneratorHelper;
import backend.datn.helpers.RandomHelper;
import backend.datn.mapper.EmployeeMapper;
import backend.datn.repositories.EmployeeRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<EmployeeResponse> getAllEmployees(String search, int page, int size, String sortBy, String sortDir) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "id";
        }

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Employee> employees = employeeRepository.searchEmployees(search, pageable);

        return employees.map(EmployeeMapper::toEmployeeResponse);
    }

    public EmployeeResponse getEmployeeById(int id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhân viên với id: " + id));
        return EmployeeMapper.toEmployeeResponse(employee);
    }

    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new EntityAlreadyExistsException("Email đã tồn tại.");
        }
        if (employeeRepository.existsByPhone(request.getPhone())) {
            throw new EntityAlreadyExistsException("Số điện thoại đã tồn tại.");
        }
        if(employeeRepository.existsByUsername(request.getUsername())){
            throw new EntityAlreadyExistsException("Tên đăng nhập đã tồn tại.");
        }

        Employee employee = new Employee();
        employee.setEmployeeCode(CodeGeneratorHelper.generateCode("EMP"));
        employee.setUsername(request.getUsername());
        employee.setFullname(request.getFullname());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setAddress(request.getAddress());
        employee.setPhoto(request.getPhoto());
        employee.setGender(request.getGender());
        employee.setCreateDate(Instant.now());
        employee.setForgetPassword(false);
        employee.setStatus(1);

        String rawPassword = RandomHelper.generateRandomString(8);
        String hashedPassword = passwordEncoder.encode(rawPassword);
        employee.setPassword(hashedPassword);

        employee = employeeRepository.save(employee);

        return EmployeeMapper.toEmployeeResponse(employee);
    }

    public EmployeeResponse updateEmployee(int id, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhân viên với id: " + id));

        if (employeeRepository.existsByEmailAndNotId(request.getEmail(), id)) {
            throw new EntityAlreadyExistsException("Email đã tồn tại.");
        }
        if (employeeRepository.existsByPhoneAndNotId(request.getPhone(), id)) {
            throw new EntityAlreadyExistsException("Số điện thoại đã tồn tại.");
        }

        employee.setFullname(request.getFullname());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setAddress(request.getAddress());
        employee.setPhoto(request.getPhoto());
        employee.setGender(request.getGender());
        employee.setUpdateDate(Instant.now());

        employee = employeeRepository.save(employee);

        return EmployeeMapper.toEmployeeResponse(employee);
    }

    public EmployeeResponse toggleStatusEmployee(int id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy nhân viên với id: " + id));

        employee.setStatus(employee.getStatus() == 1 ? 0 : 1);
        employee = employeeRepository.save(employee);
        return EmployeeMapper.toEmployeeResponse(employee);
    }

    public Optional<Employee> findById(@NotNull Integer employeeId) {
        return employeeRepository.findById(employeeId);
    }
}

