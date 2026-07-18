package thrift_keep.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private GroupEntity group;

    @ManyToOne
    private UserAccount user; // nullable for group-wide events

    private String type;
    // PAYMENT_PENDING, PAYMENT_SUBMITTED, PAYMENT_APPROVED, REMINDER

    private String message;

    private Integer cycleNumber;

    @CreationTimestamp
    private LocalDateTime createdAt;


    public Activity(GroupEntity group, UserAccount user, String type, String message, Integer cycleNumber) {
        this.group = group;
        this.user = user;
        this.type = type;
        this.message = message;
        this.cycleNumber = cycleNumber;
    }
}