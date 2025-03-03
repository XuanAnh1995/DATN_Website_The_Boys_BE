package backend.datn.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "\"size\"")
public class Size {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @jakarta.validation.constraints.Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "size_name", nullable = false, length = 100)
    private String sizeName;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status = true;

}