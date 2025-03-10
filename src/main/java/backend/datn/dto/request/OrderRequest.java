package backend.datn.dto.request;

import java.util.List;

public class OrderRequest {
    private Integer customerId;
    private Integer employeeId;
    private Integer voucherId;
    private Integer paymentMethod;
    private List<OrderDetailCreateRequest> orderDetails; // 🟢 Nhận danh sách sản phẩm từ FE

    // Getters và Setters
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Integer voucherId) {
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
