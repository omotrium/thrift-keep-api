package thrift_keep.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thrift_keep.exception.EmailAlreadyExistsException;
import thrift_keep.model.CreateUserRequest;
import thrift_keep.model.UserAccount;
import thrift_keep.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAccount createUser(CreateUserRequest request) {

        if (userRepository.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        String hashPassword = passwordEncoder.encode(request.getPassword());

        UserAccount user = UserAccount.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase())
                .password(hashPassword)
                .verified(true)
                .build();
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<UserAccount> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public UserAccount getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }


    public void savePushToken(String email, String token) {
        UserAccount user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow();

        user.setPushToken(token);
        userRepository.save(user);
    }
}
