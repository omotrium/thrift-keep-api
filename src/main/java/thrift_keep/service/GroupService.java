package thrift_keep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thrift_keep.exception.CustomiseException;
import thrift_keep.model.*;
import thrift_keep.repository.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final PaymentRepository paymentRepository;
    private  final GroupEventService groupEventService;
    private final PaymentService paymentService;
    private final PushNotificationService pushNotificationService;

    @Transactional
    public GroupEntity createGroup(
            String name,
            Double contributionAmount,
            Integer totalMembers,
            String frequency,
            LocalDate startDate,
            LocalDate endDate,
            String currency,
            String email
    ) {

        UserAccount owner = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new CustomiseException("User not found"));

        if (groupRepository.findByName(name).isPresent()) {
            throw new CustomiseException("Group name already exists");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        GroupEntity group = GroupEntity.builder()
                .name(name)
                .contributionAmount(contributionAmount)
                .totalMembers(totalMembers)
                .frequency(frequency.toUpperCase())
                .startDate(startDate)
                .endDate(endDate)
                .owner(owner)
                .currency(currency)
                .active(true)
                .build();

        GroupEntity savedGroup = groupRepository.save(group);

        // 👇 OWNER MEMBERSHIP (slot 1)
        Membership ownerMembership = new Membership();
        ownerMembership.setUser(owner);
        ownerMembership.setGroup(savedGroup);
        ownerMembership.setSlot(1);
        ownerMembership.setRole("ADMIN");
        ownerMembership.setStatus("ACTIVE");
        ownerMembership.setActive(true);
        ownerMembership.setJoinedAt(LocalDateTime.now());

        membershipRepository.save(ownerMembership);

        int cycles = calculateCycles(startDate, endDate, frequency);

        List<Membership> memberships = List.of(ownerMembership);

        List<Payment> payments = new ArrayList<>();

        for (int cycle = 1; cycle <= cycles; cycle++) {

            LocalDate dueDate = frequency.equalsIgnoreCase("WEEKLY")
                    ? startDate.plusWeeks(cycle - 1)
                    : startDate.plusMonths(cycle - 1);

            for (Membership membership : memberships) {

                Payment payment = new Payment();
                payment.setMembership(membership);
                payment.setGroup(savedGroup);
                payment.setAmount(contributionAmount);
                payment.setStatus(PaymentStatus.PENDING);
                payment.setCycleNumber(cycle);
                payment.setDueDate(dueDate);

                if (cycle == membership.getSlot()) {
                    payment.setIsPayout(true);
                }

                payments.add(payment);
            }
        }

        paymentRepository.saveAll(payments);

        return savedGroup;
    }

    private int calculateCycles(LocalDate start, LocalDate end, String frequency) {
        if (frequency.equalsIgnoreCase("WEEKLY")) {
            return (int) ChronoUnit.WEEKS.between(start, end);
        } else if (frequency.equalsIgnoreCase("MONTHLY")) {
            return (int) ChronoUnit.MONTHS.between(start, end);
        } else {
            throw new IllegalArgumentException("Invalid frequency");
        }
    }

    // List all groups
    @Transactional
    public List<GroupEntity> getAllGroups() {
        return groupRepository.findAll();
    }

    // Get group by ID
    @Transactional
    public Optional<GroupEntity> getGroupById(Long id) {
        return groupRepository.findById(id);
    }

    @Transactional
    public List<GroupResponse> getMyGroups(String email) {

        List<GroupEntity> groups = groupRepository.findAllUserGroups(email)
                .stream()
                .filter(g -> g.getStartDate() != null)
                .toList();

        return groups.stream().map(g -> {

            List<MemberResponse> members = g.getMembers()
                    .stream()
                    .map(m -> new MemberResponse(
                            m.getId(),
                            m.getUser().getName(),
                            m.getUser().getEmail(),
                            m.getUser().getId()

                    ))
                    .toList();

            // -----------------------------
            // CURRENT CYCLE
            // -----------------------------
            int currentCycle = paymentService.calculateCurrentCycle(g);

            // -----------------------------
            // COUNTS
            // -----------------------------
            int pendingPayments = paymentRepository
                    .countByGroupAndCycleNumberAndStatus(
                            g,
                            currentCycle,
                            PaymentStatus.PENDING
                    );

            int pendingApprovals = paymentRepository
                    .countByGroupAndCycleNumberAndStatus(
                            g,
                            currentCycle,
                            PaymentStatus.SUBMITTED
                    );

            return new GroupResponse(
                    g.getId(),
                    g.getName(),
                    g.getContributionAmount(),
                    g.getCurrency(),
                    g.getTotalMembers(),
                    g.getFrequency(),
                    g.getOwner().getEmail().equals(email),
                    members,
                    pendingPayments,
                    pendingApprovals

            );
        }).toList();
    }

    @Transactional
    public void addMember(Long groupId, AddMemberRequest request) {

        UserAccount user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new CustomiseException("User not found"));

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomiseException("Group not found"));

        // capacity check
        int currentSlots = membershipRepository.countByGroup(group);
        if (currentSlots >= group.getTotalMembers()) {
            throw new CustomiseException("Group is already full");
        }

        // auto-assign slot
        int slot = findNextAvailableSlot(group);

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setGroup(group);
        membership.setSlot(slot);
        membership.setRole("MEMBER");
        membership.setStatus("ACTIVE");
        membership.setActive(true);
        membership.setJoinedAt(LocalDateTime.now());

        membershipRepository.save(membership);

        generatePaymentsForMembership(group, membership);

        pushNotificationService.sendToUser(
                membership.getUser().getId(),
                "Added to a group",
                "You have been added to "
                        + group.getName()
                        + ".",
                Map.of(
                        "type",
                        "MEMBER_ADDED",

                        "groupId",
                        group.getId()
                )
        );
    }

    private int findNextAvailableSlot(GroupEntity group) {

        List<Integer> takenSlots = membershipRepository.findSlotsByGroup(group);

        Set<Integer> taken = new HashSet<>(takenSlots);

        for (int i = 1; i <= group.getTotalMembers(); i++) {
            if (!taken.contains(i)) {
                return i;
            }
        }

        throw new CustomiseException("No available slots");
    }

    private void generatePaymentsForMembership(GroupEntity group, Membership membership) {

        int cycles = calculateCycles(
                group.getStartDate(),
                group.getEndDate(),
                group.getFrequency()
        );

        List<Payment> payments = new ArrayList<>();

        for (int cycle = 1; cycle <= cycles; cycle++) {

            boolean exists = paymentRepository
                    .existsByMembershipAndCycleNumber(membership, cycle);

            if (exists) continue;

            LocalDate dueDate = group.getFrequency().equalsIgnoreCase("WEEKLY")
                    ? group.getStartDate().plusWeeks(cycle - 1)
                    : group.getStartDate().plusMonths(cycle - 1);

            Payment payment = new Payment();
            payment.setMembership(membership);
            payment.setGroup(group);
            payment.setAmount(group.getContributionAmount());
            payment.setCycleNumber(cycle);
            payment.setDueDate(dueDate);
            payment.setStatus(PaymentStatus.PENDING);

            // payout logic
            if (cycle == membership.getSlot()) {
                payment.setIsPayout(true);
            }

            payments.add(payment);
        }

        if (!payments.isEmpty()) {
            try {
                paymentRepository.saveAll(payments);
            } catch (Exception e) {
                e.printStackTrace();
                throw new CustomiseException("Failed to generate payments");
            }
        }
    }


    @Transactional
    public void deleteGroup(Long groupId, String userEmail) {

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomiseException("Group not found"));
        // Optional: ensure only creator can delete
        if (!group.getOwner().getEmail().equals(userEmail)) {
            throw new CustomiseException("Not allowed to delete this group");
        }

        List<Long> memberIds =
                group.getMembers()
                        .stream()
                        .map(member ->
                                member.getUser().getId()
                        )
                        .toList();
        pushNotificationService.sendToUsers(
                memberIds,
                "Group deleted",
                group.getName()
                        + " has been deleted.",
                Map.of(
                        "type",
                        "GROUP_DELETED",
                        "groupId",
                        group.getId()
                )
        );

        paymentRepository.deleteByGroupId(groupId);

        membershipRepository.deleteByGroupId(groupId);

        groupRepository.delete(group);
    }

    @Transactional
    public void removeMember(Long groupId, Long userId, String requesterEmail) {

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomiseException("Group not found"));

        // Only owner can remove members
        if (!group.getOwner().getEmail().equalsIgnoreCase(requesterEmail)) {
            throw new CustomiseException("You are not allowed to remove members");
        }

        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomiseException("User not found"));

        Membership membership = membershipRepository
                .findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new CustomiseException("Member not found in group"));

        //  Prevent removing owner membership
        if (membership.getUser().getId().equals(group.getOwner().getId())) {
            throw new CustomiseException("Cannot remove group owner");
        }

        pushNotificationService.sendToUser(
                membership.getUser().getId(),
                "Removed from group",
                "You have been removed from "
                        + group.getName()
                        + ".",
                Map.of(
                        "type",
                        "MEMBER_REMOVED",

                        "groupId",
                        group.getId()
                )
        );

        //  delete payments by MEMBERSHIP (correct model)
        paymentRepository.deleteByMembership(membership);

        //  delete membership itself
        membershipRepository.delete(membership);

        groupEventService.notifyGroupUpdate(groupId);
    }

}
