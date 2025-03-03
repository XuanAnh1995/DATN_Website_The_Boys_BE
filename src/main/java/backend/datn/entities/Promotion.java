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
@Table(name = "promotion")
public class Promotion {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Nationalized
    @Column(name = "promotion_name", nullable = false)
    private String promotionName;

    @NotNull
    @Column(name = "promotion_percent", nullable = false)
    private Integer promotionPercent;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private Instant startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private Instant endDate;

    @Size(max = 500)
    @NotNull
    @Nationalized
    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status = false;

}