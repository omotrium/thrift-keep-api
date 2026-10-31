package thrift_keep.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "push_token",
        indexes = {
                @Index(
                        name = "idx_push_token_token",
                        columnList = "token",
                        unique = true
                ),
                @Index(
                        name = "idx_push_token_user",
                        columnList = "user_id"
                )
        }
)
public class PushToken {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private UserAccount user;

    @Column(
            name = "token",
            nullable = false,
            unique = true,
            length = 500
    )
    private String token;

    @Column(
            name = "platform",
            length = 30
    )
    private String platform;

    @Column(
            name = "device_name",
            length = 200
    )
    private String deviceName;

    @Column(
            name = "active",
            nullable = false
    )
    private boolean active = true;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "last_seen_at",
            nullable = false
    )
    private LocalDateTime lastSeenAt;

    public PushToken() {
    }

    public Long getId() {
        return id;
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(
            LocalDateTime lastSeenAt
    ) {
        this.lastSeenAt = lastSeenAt;
    }

    @PrePersist
    public void onCreate() {
        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        lastSeenAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        lastSeenAt =
                LocalDateTime.now();
    }
}
