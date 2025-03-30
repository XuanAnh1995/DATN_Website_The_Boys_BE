package backend.datn.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"order\"")
public class OrderOnline {
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
    private LocalDateTime createDate;

    @NotNull
    @Column(name = "total_amount")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @NotNull
    @Column(name = "total_bill")
    private BigDecimal totalBill = BigDecimal.ZERO;

    @NotNull
    @Column(name = "payment_method", nullable = false)
    private Integer paymentMethod;

    @NotNull
    @Column(name = "status_order", nullable = false)
    private Integer statusOrder;

    @NotNull
    @Column(name = "kind_of_order", nullable = false)
    private Boolean kindOfOrder = false;

    @Size(max = 15)
    @Column(name = "phone", length = 15)
    private String phone;

    @Size(max = 255)
    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "shipfee", precision = 18, scale = 2)
    private BigDecimal shipfee;

    @Column(name = "discount", precision = 18, scale = 2)
    private BigDecimal discount;
}
