package thrift_keep.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thrift_keep.model.UserAccount;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmailIgnoreCase(String email);

}