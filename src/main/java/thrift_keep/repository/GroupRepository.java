package thrift_keep.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import thrift_keep.model.GroupEntity;
import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

    Optional<GroupEntity> findByName(String name);
    @Query(
            "SELECT DISTINCT g FROM GroupEntity g " +
                    "LEFT JOIN g.members m " +
                    "WHERE g.owner.email = :email " +
                    "OR m.user.email = :email")
    List<GroupEntity> findAllUserGroups(@Param("email") String email);

}
