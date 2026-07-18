package thrift_keep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thrift_keep.exception.CustomiseException;
import thrift_keep.model.*;
import thrift_keep.repository.ContributionRepository;
import thrift_keep.repository.GroupRepository;
import thrift_keep.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContributionService {

    private final ContributionRepository contributionRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public Contribution createContribution(Long groupId, String email, Double amount, String dueDate) {

        UserAccount user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new CustomiseException("User not found"));

        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new CustomiseException("Group not found"));

        Contribution contribution = new Contribution();
        contribution.setUser(user);
        contribution.setGroup(group);
        contribution.setAmount(amount);
        contribution.setDueDate(dueDate);
        contribution.setPaid(false);

        return contributionRepository.save(contribution);
    }

    public Contribution payContribution(Long id) {
        Contribution c = contributionRepository.findById(id)
                .orElseThrow();

        c.setPaid(true);
        c.setPaidAt(LocalDate.now());

        return contributionRepository.save(c);
    }

    public List<Contribution> getPending(String email) {
        return contributionRepository.findByUserEmailAndPaidFalse(email);
    }


}
