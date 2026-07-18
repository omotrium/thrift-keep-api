package thrift_keep.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import thrift_keep.model.Contribution;
import thrift_keep.model.CreateContributionRequest;
import thrift_keep.service.ContributionService;
import java.util.List;

@RestController
@RequestMapping("/api/contributions")
@RequiredArgsConstructor
public class ContributionController {

    private final ContributionService contributionService;

    @PostMapping
    public Contribution create(@RequestBody CreateContributionRequest req,
                               Authentication auth) {

        return contributionService.createContribution(
                req.getGroupId(),
                auth.getName(),
                req.getAmount(),
                req.getPaymentDate()
        );
    }

    @PostMapping("/{id}/pay")
    public Contribution pay(@PathVariable Long id) {
        return contributionService.payContribution(id);
    }

    @GetMapping("/pending")
    public List<Contribution> pending(Authentication auth) {
        return contributionService.getPending(auth.getName());
    }
}
