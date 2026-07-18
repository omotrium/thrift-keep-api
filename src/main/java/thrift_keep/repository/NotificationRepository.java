package thrift_keep.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import thrift_keep.model.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Get all notifications for a user (latest first)
    List<Notification> findByUserEmailOrderByCreatedAtDesc(String email);

//    // Get unread notifications
//    List<Notification> findByUserEmailAndReadFalse(String email);
//
//    // Count unread notifications (for badge UI)
//    long countByUserEmailAndReadFalse(String email);
//
//    // Mark all as read (bulk update)
//    @Modifying
//    @Transactional
//    @Query("UPDATE Notification n SET n.read = true WHERE n.user.email = :email")
//    void markAllAsRead(@Param("email") String email);
}
