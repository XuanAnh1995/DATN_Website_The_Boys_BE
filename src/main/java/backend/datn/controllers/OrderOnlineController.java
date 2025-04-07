package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.request.OrderOnlineRequest;
import backend.datn.dto.response.OrderOnlineResponse;
import backend.datn.dto.response.PagedResponse;
import backend.datn.exceptions.EntityNotFoundException;
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
     * @param sortKey Trường để sắp xếp (mặc định "createDate")
     * @param sortDirection Hướng sắp xếp (mặc định "desc")
     * @return ResponseEntity<ApiResponse> Kết quả phân trang
     */
    @GetMapping("/online")
    public ResponseEntity<ApiResponse> getAllOnlineOrders(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortKey,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        try {
            // Gọi service để lấy danh sách đơn hàng online với tìm kiếm và phân trang
            Page<OrderOnlineResponse> onlineOrdersPage = orderOnlineService.getAllOnlineOrders(
                    search, page, size, sortKey, sortDirection);

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

    /**
     * API lấy thông tin đơn hàng online theo ID
     * @param id ID của đơn hàng
     * @return ResponseEntity<ApiResponse> Thông tin đơn hàng online
     */
    @GetMapping("/online/{id}")
    public ResponseEntity<ApiResponse> findOrderOnlineById(@PathVariable Integer id) {
        try {
            // Gọi service để lấy thông tin đơn hàng online theo ID
            OrderOnlineResponse orderResponse = orderOnlineService.findOrderOnlineByIdWithKindOfOrder(id);

            // Tạo phản hồi thành công
            ApiResponse response = new ApiResponse("success", "Lấy thông tin đơn hàng online thành công", orderResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Tạo phản hồi lỗi
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * API lấy chi tiết đơn hàng online kèm danh sách sản phẩm
     */
    @GetMapping("/online/{id}/details")
    public ResponseEntity<ApiResponse> getOrderOnlineDetails(@PathVariable Integer id) {
        try {
            OrderOnlineResponse orderResponse = orderOnlineService.getOrderOnlineDetails(id);
            ApiResponse response = new ApiResponse("success", "Lấy chi tiết đơn hàng online thành công", orderResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi lấy chi tiết đơn hàng online", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API cập nhật trạng thái đơn hàng online
     */
    @PutMapping("/online/{id}/status")
    public ResponseEntity<ApiResponse> updateOrderStatus(
            @PathVariable Integer id,
            @RequestParam Integer newStatus) {
        try {
            OrderOnlineResponse updatedOrder = orderOnlineService.updateOrderStatus(id, newStatus);
            return ResponseEntity.ok(new ApiResponse("success", "Cập nhật trạng thái đơn hàng thành công", updatedOrder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse("error", e.getMessage()));
        }
    }
}

