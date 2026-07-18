package thrift_keep.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thrift_keep.model.Activity;
import thrift_keep.model.GroupActivityResponse;
import thrift_keep.repository.ActivityRepository;
import thrift_keep.service.ActivityService;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityRepository activityRepository;
    private final ActivityService activityService;

    @GetMapping("/groups/{groupId}/activities")
    public List<Activity> getActivities(@PathVariable Long groupId) {
        return activityRepository.findByGroupIdOrderByCreatedAtDesc(groupId);
    }

    @GetMapping("/activities/my-groups")
    public List<GroupActivityResponse> getMyGroupActivities(Authentication auth) {
        return activityService.getMyGroupActivities(auth.getName());
    }
}
