package backend.datn.controllers;

import backend.datn.dto.ApiResponse;
import backend.datn.dto.response.OrderPOSResponse;
import backend.datn.dto.response.PagedResponse;
import backend.datn.exceptions.EntityNotFoundException;
import backend.datn.services.OrderPOSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/orders")
public class OrderPOSController {

    @Autowired
    private OrderPOSService orderPOSService;

    /**
     * API lấy danh sách đơn hàng POS với tìm kiếm và phân trang
     *
     * @param search        Từ khóa tìm kiếm (tùy chọn)
     * @param page          Số trang (mặc định 0)
     * @param size          Kích thước trang (mặc định 10)
     * @param sortKey       Trường để sắp xếp (mặc định "createDate")
     * @param sortDirection Hướng sắp xếp (mặc định "desc")
     * @return ResponseEntity<ApiResponse> Kết quả phân trang
     */
    @GetMapping("/pos")
    public ResponseEntity<ApiResponse> getAllPOSOrders(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortKey,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        try {
            // Gọi service để lấy danh sách đơn hàng online với tìm kiếm và phân trang
            Page<OrderPOSResponse> orderPOSResponsePage = orderPOSService.getAllPOSOrders(search, page, size, sortKey, sortDirection);

            // Bọc dữ liệu vào PagedResponse
            PagedResponse<OrderPOSResponse> responseData = new PagedResponse<>(orderPOSResponsePage);
            // Tạo phản hồi thành công
            ApiResponse response = new ApiResponse("success", "Lấy danh sách đơn hàng POS thành công", responseData);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Tạo phản hồi lỗi
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi truy xuất danh sách đơn hàng POS", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API lấy thông tin đơn hàng POS theo ID
     * @param id ID của đơn hàng
     * @return ResponseEntity<ApiResponse> Thông tin đơn hàng POS
     */
    @GetMapping("/pos/{id}")
    public ResponseEntity<ApiResponse> findOrderPOSById(@PathVariable Long id) {
        try {
            // Gọi service để lấy thông tin đơn hàng POS theo ID
            OrderPOSResponse orderResponse = orderPOSService.getPOSOrderById(id);

            // Tạo phản hồi thành công
            ApiResponse response = new ApiResponse("success", "Lấy thông tin đơn hàng POS thành công", orderResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // Tạo phản hồi lỗi
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * API lấy chi tiết đơn hàng POS kèm danh sách sản phẩm
     */
    @GetMapping("/pos/{id}/details")
    public ResponseEntity<ApiResponse> getOrderPOSDetails(@PathVariable Long id) {
        try {
            OrderPOSResponse orderResponse = orderPOSService.getOrderPOSDetails(id);
            ApiResponse response = new ApiResponse("success", "Lấy chi tiết đơn hàng POS thành công", orderResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            ApiResponse response = new ApiResponse("error", e.getMessage(), null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ApiResponse response = new ApiResponse("error", "Đã xảy ra lỗi khi lấy chi tiết đơn hàng POS", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
