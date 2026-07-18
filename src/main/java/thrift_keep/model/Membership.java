package thrift_keep.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "membership")
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserAccount user;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    @ToString.Exclude
    private GroupEntity group;

    private Integer payoutOrder;

    @Builder.Default
    private Boolean active = true;

    private String role; // ADMIN, MEMBER

    private String status; // ACTIVE, PENDING

    private Integer slot;

    private LocalDateTime joinedAt;


//    public Membership(UserAccount user, GroupEntity group) {
//        this.user = user;
//        this.group = group;
//    }
}
