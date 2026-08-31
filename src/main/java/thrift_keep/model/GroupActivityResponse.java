package thrift_keep.model;

import java.time.LocalDateTime;


public class GroupActivityResponse {

    private Long groupId;
    private String groupName;
    private String message;
    private LocalDateTime createdAt;
    private Double totalContributed;

    public GroupActivityResponse(Long groupId, String groupName, String message, LocalDateTime createdAt, Double totalContributed) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.message = message;
        this.createdAt = createdAt;
        this.totalContributed = totalContributed;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getTotalContributed() {
        return totalContributed;
    }

    public void setTotalContributed(Double totalContributed) {
        this.totalContributed = totalContributed;
    }
}
