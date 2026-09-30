package thrift_keep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import thrift_keep.exception.CustomiseException;
import thrift_keep.model.Notification;
import thrift_keep.model.UserAccount;
import thrift_keep.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public void createNotification(UserAccount user, String message) {

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    // Get current user's notifications
    public List<Notification> getMyNotifications(Authentication authentication) {
        String email = authentication.getName();

        return notificationRepository
                .findByUserEmailOrderByCreatedAtDesc(email);
    }

    // Mark notification as read
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new CustomiseException("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public void sendPush(String expoPushToken, String title, String body) {
        String url = "https://exp.host/--/api/v2/push/send";

        Map<String, Object> message = new HashMap<>();
        message.put("to", expoPushToken);
        message.put("title", title);
        message.put("body", body);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(message, headers);

        restTemplate.postForEntity(url, request, String.class);
    }
}
