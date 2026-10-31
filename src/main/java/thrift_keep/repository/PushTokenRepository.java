package thrift_keep.repository;

import thrift_keep.model.PushToken;
import thrift_keep.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushTokenRepository
        extends JpaRepository<PushToken, Long> {

    Optional<PushToken> findByToken(String token);

    List<PushToken> findByUserAndActiveTrue(UserAccount user);

    List<PushToken> findByUserIdAndActiveTrue(Long userId);
}
