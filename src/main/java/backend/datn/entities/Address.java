package backend.datn.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @NotNull
    @Column(name = "province_id", nullable = false)
    private Integer provinceId;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "province_name", nullable = false, length = 50)
    private String provinceName;

    @NotNull
    @Column(name = "district_id", nullable = false)
    private Integer districtId;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "district_name", nullable = false, length = 50)
    private String districtName;

    @NotNull
    @Column(name = "ward_id", nullable = false)
    private Integer wardId;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "ward_name", nullable = false, length = 50)
    private String wardName;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "address_detail", nullable = false)
    private String addressDetail;

}