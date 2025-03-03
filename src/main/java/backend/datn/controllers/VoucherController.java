package backend.datn.controllers;



import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.VoucherCreateRequest;
import backend.datn.dto.request.VoucherUpdateRequest;
import backend.datn.dto.response.VoucherResponse;
import backend.datn.exceptions.EntityAlreadyExistsException;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.services.VoucherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {
    @Autowired private VoucherService voucherService;


    @GetMapping
    public ResponseEntity<ApiResponse> getALlVoucher(@RequestParam(required = false) String search,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10")int size,
                                                     @RequestParam(defaultValue = "id") String id,
                                                     @RequestParam(defaultValue = "asc")String sort){
        try {
            Page<VoucherResponse> voucherResponses = voucherService.getAllVoucher(search, page, size, id, sort);
            ApiResponse response = new ApiResponse("success", "Lấy danh sách voucher thành công", voucherResponses);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Lỗi khi lấy danh sách voucher", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getVoucherById(@PathVariable Integer id) {
        try {
            VoucherResponse voucherRespone = voucherService.getVoucherById(id);
            ApiResponse response = new ApiResponse("success", "Lấy voucher thành công", voucherRespone);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Lỗi khi lấy voucher", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //    @PostMapping
//    public ResponseEntity<ApiResponse> createVoucher(@RequestBody VoucherCreateRequest voucherRequest) {
//        try {
//            VoucherRespone voucherRespone = voucherService.createVoucher(voucherRequest);
//            ApiResponse response = new ApiResponse("success", "Tạo voucher thành công", voucherRespone);
//            return new ResponseEntity<>(response, HttpStatus.CREATED);
//        } catch (EntityAlreadyExistsException e) {
//            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
//            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
//        } catch (Exception e) {
//            ApiResponse response = new ApiResponse("error", "Không thể tạo voucher", null);
//            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//                }
//    }
    @PostMapping
    public ResponseEntity<ApiResponse> createVoucher(@Valid @RequestBody VoucherCreateRequest voucherRequest, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(new ApiResponse("error", "Dữ liệu không hợp lệ", errors));
        }
        try {
            VoucherResponse voucherRespone = voucherService.createVoucher(voucherRequest);
            ApiResponse response = new ApiResponse("success", "Tạo voucher thành công", voucherRespone);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (EntityAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("error", "Không thể tạo voucher", null));
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateVoucher(@PathVariable Integer id,@Valid  @RequestBody VoucherUpdateRequest updateRequestvoucherRequest,BindingResult result) {
        try {
            VoucherResponse respons = voucherService.updateVoucher(updateRequestvoucherRequest,id);
            ApiResponse response = new ApiResponse("success", "Update  successfully", updateRequestvoucherRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch(EntityNotFoundException e){
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        catch (EntityAlreadyExistsException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "An error", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteVoucher(@PathVariable Integer id) {
        try {
            voucherService.deleteVoucher(id);
            return  ResponseEntity.ok(new ApiResponse("success", "Xóa voucher thành công", null));
        } catch (EntityNotFoundException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Khong the xoa voucher", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse> toggleStatusVoucher(@PathVariable Integer id) {
        try {
            VoucherResponse voucherRespone = voucherService.toggleStatusVoucher(id);
            ApiResponse response = new ApiResponse("success", "Thành Công Thay Đổi ", voucherRespone);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Lỗi khi thay đổi trạng thái", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


