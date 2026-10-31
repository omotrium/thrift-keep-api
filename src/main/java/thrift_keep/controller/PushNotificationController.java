package thrift_keep.controller;


import thrift_keep.model.PushToken;
import thrift_keep.model.RegisterPushTokenRequest;
import thrift_keep.model.UserAccount;
import thrift_keep.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import thrift_keep.service.PushNotificationService;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class PushNotificationController {

    private final PushNotificationService
            pushNotificationService;

    private final UserRepository
            userRepository;



    public PushNotificationController(PushNotificationService pushNotificationService, UserRepository userRepository) {
        this.pushNotificationService = pushNotificationService;
        this.userRepository = userRepository;
    }

    /* =====================================================
       REGISTER DEVICE
    ===================================================== */

    @PostMapping("/register-token")
    public ResponseEntity<?> registerToken(
            @RequestBody
                    RegisterPushTokenRequest request,
            Authentication authentication
    ) {
        UserAccount user =
                getAuthenticatedUser(
                        authentication
                );

        PushToken token =
                pushNotificationService
                        .registerToken(
                                user,
                                request.getToken(),
                                request.getPlatform(),
                                request.getDeviceName()
                        );

        return ResponseEntity.ok(
                Map.of(
                        "success",
                        true,
                        "id",
                        token.getId()
                )
        );
    }

    /* =====================================================
       REMOVE DEVICE
    ===================================================== */

    @DeleteMapping("/register-token")
    public ResponseEntity<?> removeToken(
            @RequestBody
                    RegisterPushTokenRequest request,
            Authentication authentication
    ) {
        UserAccount user =
                getAuthenticatedUser(
                        authentication
                );

        pushNotificationService
                .removeToken(
                        user,
                        request.getToken()
                );

        return ResponseEntity.ok(
                Map.of(
                        "success",
                        true
                )
        );
    }

    /* =====================================================
       TEST NOTIFICATION
    ===================================================== */

    @PostMapping("/test")
    public ResponseEntity<?> test(
            Authentication authentication
    ) {
        UserAccount user =
                getAuthenticatedUser(
                        authentication
                );

        pushNotificationService.sendToUser(
                user.getId(),
                "ThriftKeep",
                "Push notifications are working!",
                Map.of(
                        "type",
                        "TEST"
                )
        );

        return ResponseEntity.ok(
                Map.of(
                        "success",
                        true
                )
        );
    }

    /* =====================================================
       AUTHENTICATED USER
    ===================================================== */

    private UserAccount getAuthenticatedUser(
            Authentication authentication
    ) {
        String email =
                authentication
                        .getName()
                        .trim()
                        .toLowerCase();

        return userRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        "Authenticated user not found"
                                )
                );
    }
}
