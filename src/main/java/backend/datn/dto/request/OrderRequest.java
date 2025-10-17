package backend.datn.dto.request;

import java.util.List;

public class OrderRequest {
    private Long customerId;
    private Long employeeId;
    private Long voucherId;
    private Integer paymentMethod;
    private List<OrderDetailCreateRequest> orderDetails; // 🟢 Nhận danh sách sản phẩm từ FE

    // Getters và Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Long voucherId) {
        this.voucherId = voucherId;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<OrderDetailCreateRequest> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailCreateRequest> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
