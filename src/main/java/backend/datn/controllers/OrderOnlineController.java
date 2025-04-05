package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.OrderOnlineRequest;
import backend.datn.dto.response.OrderOnlineResponse;
import backend.datn.dto.response.PagedResponse;
import backend.datn.services.OrderOnlineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderOnlineController {

    @Autowired
    private OrderOnlineService orderOnlineService;

    /**
     * API tạo đơn hàng online
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createOrder(@Valid @RequestBody OrderOnlineRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                    .orElse("Dữ liệu không hợp lệ");

            return ResponseEntity.badRequest().body(new ApiResponse("error", errorMessage));
        }

        try {
            OrderOnlineResponse orderResponse = orderOnlineService.createOrder(request);
            return ResponseEntity.ok(new ApiResponse("success", "Đơn hàng đã được tạo thành công", orderResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        }
    }

    /**
     * API lấy danh sách đơn hàng online với tìm kiếm và phân trang
     * @param search Từ khóa tìm kiếm (tùy chọn)
     * @param page Số trang (mặc định 0)
     * @param size Kích thước trang (mặc định 10)
     * @param sortBy Trường để sắp xếp (mặc định "createDate")
     * @param sortDir Hướng sắp xếp (mặc định "desc")
     * @return ResponseEntity<ApiResponse> Kết quả phân trang
     */
    @GetMapping("/online")
    public ResponseEntity<ApiResponse> getAllOnlineOrders(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            // Gọi service để lấy danh sách đơn hàng online với tìm kiếm và phân trang
            Page<OrderOnlineResponse> onlineOrdersPage = orderOnlineService.getAllOnlineOrders(
                    search, page, size, sortBy, sortDir);

            // Bọc dữ liệu vào PagedResponse
            PagedResponse<OrderOnlineResponse> responseData = new PagedResponse<>(onlineOrdersPage);

            // Tạo phản hồi thành công
            ApiResponse response = new ApiResponse("success", "Lấy danh sách đơn hàng online thành công", responseData);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Tạo phản hồi lỗi
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi truy xuất danh sách đơn hàng online", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

