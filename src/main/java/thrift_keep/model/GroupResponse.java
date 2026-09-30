package thrift_keep.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GroupResponse {
    private Long id;
    private String name;
    private Double contributionAmount;
    private String currency;
    private Integer totalMembers;
    private String frequency;
    private boolean isOwner;

    private List<MemberResponse> members;

    private int pendingPayments;
    private int pendingApprovals;

    public GroupResponse(Long id, String name, Double contributionAmount, String currency, Integer totalMembers, String frequency, boolean isOwner, List<MemberResponse> members, int pendingPayments, int pendingApprovals) {
        this.id = id;
        this.name = name;
        this.contributionAmount = contributionAmount;
        this.currency = currency;
        this.totalMembers = totalMembers;
        this.frequency = frequency;
        this.isOwner = isOwner;
        this.members = members;
        this.pendingPayments = pendingPayments;
        this.pendingApprovals = pendingApprovals;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getContributionAmount() {
        return contributionAmount;
    }

    public void setContributionAmount(Double contributionAmount) {
        this.contributionAmount = contributionAmount;
    }

    public Integer getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(Integer totalMembers) {
        this.totalMembers = totalMembers;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public List<MemberResponse> getMembers() {
        return members;
    }

    public void setMembers(List<MemberResponse> members) {
        this.members = members;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
