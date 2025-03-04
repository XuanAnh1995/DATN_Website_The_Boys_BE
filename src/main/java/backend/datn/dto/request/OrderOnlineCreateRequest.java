package backend.datn.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderOnlineCreateRequest {

    @NotNull
    private Integer employeeId;

    private Integer voucherId;

    @NotNull
    private Integer customerId;

    @NotNull
    private Integer paymentMethod;

    @NotNull
    private Boolean kindOfOrder = true;

    @NotNull
    private Integer statusOrder = 0;

    // Thêm danh sách chi tiết sản phẩm trong đơn hàng
    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    private List<OrderDetailCreateRequest> orderDetails;

    // ✅ Trả về tổng số lượng sản phẩm
    public Integer totalAmount() {
        if (orderDetails == null || orderDetails.isEmpty()) {
            throw new IllegalStateException("Không có sản phẩm nào trong đơn hàng.");
        }
        return orderDetails.stream()
                .mapToInt(OrderDetailCreateRequest::getQuantity)
                .sum();
    }

}
