//package thrift_keep.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import thrift_keep.model.Contribution;
//import thrift_keep.model.GroupEntity;
//import thrift_keep.model.Membership;
//
//import java.math.BigDecimal;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class PayoutCycleService {
//
//    private final ContributionService contributionService;
//    private final MembershipService membershipService;
//
//    /**
//     * Calculate who should receive payout for a group in this cycle.
//     * The next member in payout order who hasn't received payout yet will be selected.
//     */
//    @Transactional
//    public Optional<Membership> calculateNextPayout(Long groupId) {
//        // Get all members of the group sorted by payoutOrder
//        List<Membership> members = membershipService.getMembersByGroupIdSorted(groupId);
//
//        for (Membership member : members) {
//            // Check if this member already received payout for the current cycle
//            List<Contribution> contributions = contributionService.getContributionsByMembership(member);
//
//            boolean allContributionsPaid = contributions.stream().allMatch(Contribution::getPaidOut);
//
//            if (!allContributionsPaid) {
//                // This member is next in line to receive payout
//                return Optional.of(member);
//            }
//        }
//
//        // Everyone has been paid, return empty
//        return Optional.empty();
//    }
//
//    /**
//     * Execute payout: mark all contributions for the member as paid
//     */
//    @Transactional
//    public BigDecimal executePayout(Membership member) {
//        List<Contribution> contributions = contributionService.getContributionsByMembership(member);
//
//        BigDecimal payoutAmount = contributions.stream()
//                .filter(c -> !c.getPaidOut())
//                .map(Contribution::getAmount)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        // Mark contributions as paid
//        contributions.stream()
//                .filter(c -> !c.getPaidOut())
//                .forEach(contributionService::markAsPaid);
//
//        return payoutAmount; // can be sent to the member externally
//    }
//}
