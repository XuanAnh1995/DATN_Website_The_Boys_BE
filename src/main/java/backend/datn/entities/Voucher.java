package backend.datn.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "voucher")
public class Voucher {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Column(name = "voucher_code", nullable = false, length = 50)
    private String voucherCode;

    @Size(max = 250)
    @NotNull
    @Nationalized
    @Column(name = "voucher_name", nullable = false, length = 250)
    private String voucherName;

    @Size(max = 255)
    @Nationalized
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "min_condition", nullable = false, precision = 18, scale = 2)
    private BigDecimal minCondition;

    @NotNull
    @Column(name = "max_discount", nullable = false, precision = 18, scale = 2)
    private BigDecimal maxDiscount;

    @NotNull
    @Column(name = "reduced_percent", nullable = false)
    private Double reducedPercent;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status = false;

}