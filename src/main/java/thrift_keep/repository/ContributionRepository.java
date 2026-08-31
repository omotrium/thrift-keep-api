package thrift_keep.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import thrift_keep.model.Contribution;
import thrift_keep.model.Membership;

import java.util.List;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {

        List<Contribution> findByUserEmailAndPaidFalse(String email);

}

