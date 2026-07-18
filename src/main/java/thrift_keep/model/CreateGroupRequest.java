package thrift_keep.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateGroupRequest {
    private String name;
    private Double contributionAmount;
    private Integer totalMembers;
    private String frequency; // WEEKLY / MONTHLY
    private LocalDate startDate;
    private LocalDate endDate;
}
