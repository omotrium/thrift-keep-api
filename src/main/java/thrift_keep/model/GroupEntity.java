package thrift_keep.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "group_entity")
public class GroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double contributionAmount; // e.g., 100.0
    private Integer totalMembers;      // e.g., 5
    private String frequency;          // WEEKLY / MONTHLY
    private Boolean active;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    private UserAccount owner;

    @OneToMany(mappedBy = "group", orphanRemoval = true)
    @ToString.Exclude
    @JsonIgnore
    private List<Membership> members;

}