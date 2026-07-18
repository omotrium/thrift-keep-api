package thrift_keep.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thrift_keep.model.Membership;
import thrift_keep.model.GroupEntity;
import thrift_keep.model.UserAccount;
import thrift_keep.service.MembershipService;
import thrift_keep.service.GroupService;
import thrift_keep.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;
    private final UserService userService;
    private final GroupService groupService;

    // Join a group
    @PostMapping("/join")
    public ResponseEntity<Membership> joinGroup(@RequestParam Long userId,
                                                @RequestParam Long groupId) {
        UserAccount user = userService.getUserById(userId).orElseThrow(() ->
                new RuntimeException("User not found"));
        GroupEntity group = groupService.getGroupById(groupId).orElseThrow(() ->
                new RuntimeException("Group not found"));

        Membership membership = membershipService.joinGroup(user, group);
        return ResponseEntity.ok(membership);
    }

    // List members of a group
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Membership>> getGroupMembers(@PathVariable Long groupId) {
        GroupEntity group = groupService.getGroupById(groupId).orElseThrow(() ->
                new RuntimeException("Group not found"));
        List<Membership> members = membershipService.getGroupMembers(group);
        return ResponseEntity.ok(members);
    }

}
