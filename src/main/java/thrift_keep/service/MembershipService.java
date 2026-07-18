package thrift_keep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thrift_keep.model.Membership;
import thrift_keep.model.GroupEntity;
import thrift_keep.model.UserAccount;
import thrift_keep.repository.MembershipRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;

    @Transactional
    public Membership joinGroup(UserAccount user, GroupEntity group) {
        // Count current members to determine payout order
        int currentMembers = membershipRepository.countByGroup(group);
        int payoutOrder = currentMembers + 1;

        Membership membership = Membership.builder()
                .user(user)
                .group(group)
                .payoutOrder(payoutOrder)
                .active(true)
                .build();

        return membershipRepository.save(membership);
    }

    // Optional: list all members in a group
    public java.util.List<Membership> getGroupMembers(GroupEntity group) {
        return membershipRepository.findByGroup(group);
    }

    @Transactional(readOnly = true)
    public Optional<Membership> getMembershipById(Long id) {
        return membershipRepository.findById(id);
    }

//    @Transactional(readOnly = true)
//    public List<Membership> getMembersByGroupIdSorted(Long group) {
//        return membershipRepository.findByGroup(group);
//    }
}
