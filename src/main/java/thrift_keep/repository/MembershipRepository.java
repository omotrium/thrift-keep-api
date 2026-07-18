package thrift_keep.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import thrift_keep.model.GroupEntity;
import thrift_keep.model.Membership;
import thrift_keep.model.UserAccount;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByGroup(GroupEntity group);

    int countByGroup(GroupEntity group);

    List<Membership> findByActiveTrue();

    boolean existsByUserAndGroup(UserAccount user, GroupEntity group);

    @Modifying
    @Query("DELETE FROM Membership m WHERE m.user = :user AND m.group = :group")
    void deleteByUserAndGroup(@Param("user") UserAccount user,
                              @Param("group") GroupEntity group);

    Optional<Membership> findByGroupIdAndUserId(Long groupId, Long userId);

    void deleteByGroupId(Long groupId);

    boolean existsByGroupAndSlot(GroupEntity group, Integer slot);

    @Query("SELECT m.slot FROM Membership m WHERE m.group = :group")
    List<Integer> findSlotsByGroup(@Param("group") GroupEntity group);
}