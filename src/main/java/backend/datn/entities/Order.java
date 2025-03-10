package backend.datn.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "\"order\"")
public class Order {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @JsonIgnore
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    @JsonIgnore
    private Voucher voucher;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    private Customer customer;

    @Size(max = 50)
    @NotNull
    @Column(name = "order_code", nullable = false, length = 50)
    private String orderCode;

    @NotNull
    @Column(name = "create_date", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private Instant createDate;

    @NotNull
    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @NotNull
    @Column(name = "total_bill", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalBill;

    @NotNull
    @Column(name = "payment_method", nullable = false)
    private Integer paymentMethod;

    @NotNull
    @Column(name = "status_order", nullable = false)
    private Integer statusOrder;

    @NotNull
    @Column(name = "kind_of_order", nullable = false)
    private Boolean kindOfOrder = false;


    // Danh sách OrderDetail
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<OrderDetail> orderDetails = new ArrayList<>();

    // Phương thức set để đảm bảo quan hệ 2 chiều
    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails.clear();
        if (orderDetails != null) {
            for (OrderDetail orderDetail : orderDetails) {
                orderDetail.setOrder(this);
                this.orderDetails.add(orderDetail);
            }
        }
    }
}