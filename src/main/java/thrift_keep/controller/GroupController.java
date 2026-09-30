package thrift_keep.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import thrift_keep.model.AddMemberRequest;
import thrift_keep.model.CreateGroupRequest;
import thrift_keep.model.GroupEntity;
import thrift_keep.model.GroupResponse;
import thrift_keep.service.GroupService;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    // Create a group
    @PostMapping
    public ResponseEntity<GroupEntity> createGroup(@RequestBody CreateGroupRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName(); // comes from JWT


        GroupEntity group = groupService.createGroup(
                request.getName(),
                request.getContributionAmount(),
                request.getTotalMembers(),
                request.getFrequency(),
                request.getStartDate(),
                request.getEndDate(),
                request.getCurrency(),
                email
        );
        return ResponseEntity.ok(group);
    }

    // List all groups
    @GetMapping
    public List<GroupEntity> getAllGroups() {
        return groupService.getAllGroups();
    }

    // Get group by ID
    @GetMapping("/{id}")
    public ResponseEntity<GroupEntity> getGroup(@PathVariable Long id) {
        return groupService.getGroupById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my")
    public List<GroupResponse> getMyGroups(Authentication authentication) {
        return groupService.getMyGroups(authentication.getName());
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<?> addMember(
            @PathVariable Long groupId,
            @RequestBody AddMemberRequest request
    ) {
        groupService.addMember(groupId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long groupId,
            Authentication authentication
    ) {
        groupService.deleteGroup(groupId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long userId,
            Authentication auth
    ) {
        groupService.removeMember(groupId, userId, auth.getName());
        return ResponseEntity.noContent().build();
    }

}
