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
@Table(name = "sleeve")
public class Sleeve {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "sleeve_name", nullable = false, length = 100)
    private String sleeveName;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status = true;

}