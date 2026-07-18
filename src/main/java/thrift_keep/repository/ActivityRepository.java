package thrift_keep.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thrift_keep.model.Activity;
import thrift_keep.model.GroupEntity;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {


    List<Activity> findByGroupIdOrderByCreatedAtDesc(Long groupId);

    List<Activity> findTop5ByGroupOrderByCreatedAtDesc(GroupEntity group);
}
