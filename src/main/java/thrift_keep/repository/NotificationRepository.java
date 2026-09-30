package thrift_keep.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thrift_keep.model.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Get all notifications for a user (latest first)
    List<Notification> findByUserEmailOrderByCreatedAtDesc(String email);

}
