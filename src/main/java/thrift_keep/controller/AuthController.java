package thrift_keep.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import thrift_keep.config.OtpUtil;
import thrift_keep.exception.CustomiseException;
import thrift_keep.model.CreateUserRequest;
import thrift_keep.model.ResetPasswordRequest;
import thrift_keep.model.UserAccount;
import thrift_keep.repository.UserRepository;
import thrift_keep.service.EmailService;
import thrift_keep.service.GoogleService;
import thrift_keep.service.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import thrift_keep.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

//    private final UserService userService;
//
//    @PostMapping("/user")
//    public ResponseEntity<UserAccount> createUser(@RequestBody CreateUserRequest request) {
//        System.out.println("POST /api/users called with: {}"+ request);
//        UserAccount user = userService.createUser(request);
//        return ResponseEntity.ok(user);
//    }
//
//    @GetMapping("/all")
//    public List<UserAccount> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    @PostMapping("/test")
//    public ResponseEntity<UserAccount> testSave(@RequestBody CreateUserRequest request) {
//        System.out.println("+++++++++++++++++++++");
//        UserAccount user = UserAccount.builder()
//                .name(request.getName())
//                .email(request.getEmail())
//                .password(request.getPassword())
//                .verified(true)
//                .build();
//        UserAccount saved = userRepository.save(user);
//        System.out.println("Saved user ID: " + saved.getId());
//        return ResponseEntity.ok(saved);
//    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) throws Exception {

            if (request.containsKey("email") && request.containsKey("password")) {
            String email = request.get("email");
            String password = request.get("password");

            UserAccount user = userRepository.findByEmailIgnoreCase(email)
                        .orElseThrow(() -> new CustomiseException("Invalid credentials"));

             if (!passwordEncoder.matches(password, user.getPassword())) {
                    throw new CustomiseException("Invalid credentials");
                }

            String token = jwtService.generateToken(user.getEmail());
            return ResponseEntity.ok(Map.of("token", token));
        }

        return ResponseEntity.badRequest().body(Map.of("error", "Provide idToken or email/password"));
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        Optional<UserAccount> userOpt = userRepository.findByEmailIgnoreCase(email);

        // 🔒 Always return success (security)
        if (userOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "If email exists, OTP sent"));
        }

        UserAccount user = userOpt.get();
//
//        String otp = OtpUtil.generateOtp();
//
//        user.setResetOtp(otp);
//        user.setResetOtpExpiry(LocalDateTime.now().plusMinutes(10));

        String otp = String.valueOf(new Random().nextInt(900000) + 100000);

// store HASH instead of raw OTP
        user.setResetOtpHash(passwordEncoder.encode(otp));

        user.setResetOtpAttempts(0);
        user.setResetOtpExpiry(LocalDateTime.now().plusMinutes(10));
        user.setResetOtpCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);

        return ResponseEntity.ok(Map.of("message", "OTP sent"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {

        // ✅ Validate input
        if (request.getEmail() == null ||
                request.getOtp() == null ||
                request.getNewPassword() == null ||
                request.getNewPassword().isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Missing required fields"));
        }

        // ✅ Find user
        Optional<UserAccount> userOpt =
                userRepository.findByEmailIgnoreCase(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid request"));
        }

        UserAccount user = userOpt.get();

        // ✅ Max retry check (int → no null check needed)
        if (user.getResetOtpAttempts() >= 3) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Too many invalid attempts. Request new OTP."));
        }

        // ✅ Expiry check
        if (user.getResetOtpExpiry() == null ||
                user.getResetOtpExpiry().isBefore(LocalDateTime.now())) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", "OTP expired"));
        }

        // ✅ Verify OTP using HASH
        boolean otpMatches = passwordEncoder.matches(
                request.getOtp(),
                user.getResetOtpHash()
        );

        if (!otpMatches) {
            user.setResetOtpAttempts(user.getResetOtpAttempts() + 1);
            userRepository.save(user);

            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid OTP"));
        }

        // ✅ SUCCESS → update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // ✅ Clear OTP data
        user.setResetOtpHash(null);
        user.setResetOtpExpiry(null);
        user.setResetOtpAttempts(0);

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password reset successful"));
    }
}