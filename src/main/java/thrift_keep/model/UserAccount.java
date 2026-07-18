package thrift_keep.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
//@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_account")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String pushToken;

    @Builder.Default
    private Boolean verified = false;

    @Column(name = "reset_otp")
    private String resetOtp;

    @Column(name = "reset_otp_expiry")
    private LocalDateTime resetOtpExpiry;

    private LocalDateTime resetOtpCreatedAt;

    private String resetOtpHash;
    private int resetOtpAttempts = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getResetOtp() {
        return resetOtp;
    }

    public void setResetOtp(String resetOtp) {
        this.resetOtp = resetOtp;
    }

    public LocalDateTime getResetOtpExpiry() {
        return resetOtpExpiry;
    }

    public void setResetOtpExpiry(LocalDateTime resetOtpExpiry) {
        this.resetOtpExpiry = resetOtpExpiry;
    }

    public LocalDateTime getResetOtpCreatedAt() {
        return resetOtpCreatedAt;
    }

    public void setResetOtpCreatedAt(LocalDateTime resetOtpCreatedAt) {
        this.resetOtpCreatedAt = resetOtpCreatedAt;
    }

    public String getResetOtpHash() {
        return resetOtpHash;
    }

    public void setResetOtpHash(String resetOtpHash) {
        this.resetOtpHash = resetOtpHash;
    }

    public int getResetOtpAttempts() {
        return resetOtpAttempts;
    }

    public void setResetOtpAttempts(int resetOtpAttempts) {
        this.resetOtpAttempts = resetOtpAttempts;
    }
}