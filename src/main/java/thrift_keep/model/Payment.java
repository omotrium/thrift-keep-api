package thrift_keep.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Group this payment belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

//    // User who should pay
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id", nullable = false)
    private Membership membership;//


    // Cycle (week or month number)
    private int cycleNumber;

    // Amount to pay (can copy from group)
    private Double amount;

    // When payment is due
    private LocalDate dueDate;

    // Status
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    // Who approved (optional)
    private String approvedBy;

    // When approved
    private LocalDate approvedAt;

    private LocalDate submittedAt;

    @Column(name = "last_notified_cycle")
    private Integer lastNotifiedCycle;

    private Boolean isPayout = false;
}
