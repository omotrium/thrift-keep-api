package thrift_keep.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thrift_keep.model.Contribution;
import thrift_keep.model.Membership;

import java.util.List;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {

        List<Contribution> findByUserEmail(String email);

        List<Contribution> findByGroupId(Long groupId);

        List<Contribution> findByUserEmailAndPaidFalse(String email);
    }

