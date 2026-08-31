package thrift_keep.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import thrift_keep.model.*;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

   List<Payment> findByGroupId(Long groupId);

    @Query(
            "SELECT p FROM Payment p " +
                    "WHERE p.group.id = :groupId " +
                    "AND (p.group.owner.email = :email " +
                    "OR p.membership.user.email = :email)"
    )
    List<Payment> findPaymentsForUser(
            @Param("groupId") Long groupId,
            @Param("email") String email
    );

    void deleteByMembership(Membership membership);

    boolean existsByMembershipAndCycleNumber(Membership membership, int cycle);

    int countByGroupAndCycleNumberAndStatus(
            GroupEntity group,
            int cycle,
            PaymentStatus status
    );

    @Query(
            "SELECT p FROM Payment p " +
                    "JOIN FETCH p.membership m " +
                    "JOIN FETCH m.user " +
                    "WHERE p.group = :group " +
                    "AND p.cycleNumber = :cycle " +
                    "AND p.status = :status"
    )
    List<Payment> findByGroupAndCycleNumberAndStatus(
            @Param("group") GroupEntity group,
            @Param("cycle") int cycle,
            @Param("status") PaymentStatus status
    );

    void deleteByGroupId(Long groupId);

    @Query(
            "SELECT p FROM Payment p " +
                    "WHERE p.status = thrift_keep.model.PaymentStatus.PENDING " +
                    "AND p.dueDate BETWEEN :startOfWeek AND :endOfWeek " +
                    "AND p.group.active = true " +
                    "AND p.group.startDate <= :today " +
                    "AND p.group.endDate >= :today"
    )
    List<Payment> findPendingPaymentsForWeek(
            @Param("startOfWeek") LocalDate startOfWeek,
            @Param("endOfWeek") LocalDate endOfWeek,
            @Param("today") LocalDate today);

    @Query(
            "SELECT p FROM Payment p " +
                    "WHERE p.status = thrift_keep.model.PaymentStatus.PENDING " +
                    "AND p.dueDate < :startOfWeek " +
                    "AND p.group.active = true " +
                    "AND p.group.startDate <= :today " +
                    "AND p.group.endDate >= :today"
    )
    List<Payment> findOverduePendingPayments(
            @Param("startOfWeek") LocalDate startOfWeek,
            @Param("today") LocalDate today);



    @Query("SELECT COALESCE(SUM(p.amount), 0) " +
         "FROM Payment p " +
         "WHERE p.membership.user.email = :email " +
         "AND p.status = 'APPROVED'")
    Double getTotalContributedByUser(@Param("email") String email);
}
