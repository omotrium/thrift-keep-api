package thrift_keep.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import thrift_keep.model.CreateUserRequest;
import thrift_keep.model.UserAccount;
import thrift_keep.repository.UserRepository;
import thrift_keep.service.UserService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;

    @PostMapping("/user")
    public ResponseEntity<UserAccount> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserAccount user = userService.createUser(request);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/all")
    public List<UserAccount> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/email")
    public ResponseEntity<?> getUserByEmail(Authentication authentication) {
        UserAccount user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(user.getName());
    }

    @PostMapping("/push-token")
    public void savePushToken(
            @RequestBody Map<String, String> body,
            Authentication auth
    ) {
        userService.savePushToken(auth.getName(), body.get("token"));
    }


}
