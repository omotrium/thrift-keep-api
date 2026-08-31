package thrift_keep.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thrift_keep.model.Activity;
import thrift_keep.model.GroupActivityResponse;
import thrift_keep.model.GroupEntity;
import thrift_keep.repository.ActivityRepository;
import thrift_keep.repository.GroupRepository;
import thrift_keep.repository.PaymentRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ActivityService {

    private final GroupRepository groupRepository;
    private final ActivityRepository activityRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public List<GroupActivityResponse> getMyGroupActivities(String email) {

        List<GroupEntity> groups = groupRepository.findAllUserGroups(email);

        Double totalContributed =
                paymentRepository.getTotalContributedByUser(email);

        List<GroupActivityResponse> result = new ArrayList<>();

        for (GroupEntity g : groups) {

            List<Activity> activities = activityRepository
                    .findTop5ByGroupOrderByCreatedAtDesc(g);

            for (Activity a : activities) {
                result.add(new GroupActivityResponse(
                        g.getId(),
                        g.getName(),
                        a.getMessage(),
                        a.getCreatedAt(),
                        totalContributed
                ));
            }
        }

        // ✅ sort globally (latest first)
        result.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

        return result;
    }
}
