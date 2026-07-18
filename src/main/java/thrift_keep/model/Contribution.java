package thrift_keep.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "contribution")
@Entity
public class Contribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private String dueDate;

    private boolean paid;

    private LocalDate paidAt;

    @ManyToOne
    private UserAccount user;

    @ManyToOne
    private GroupEntity group;

    // getters/setters
}
