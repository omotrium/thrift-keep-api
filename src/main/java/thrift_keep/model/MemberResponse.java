package thrift_keep.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class MemberResponse {
    private Long id;
    private String name;
    private String email;
    private Long userId;


    public MemberResponse(Long id, String name, String email, Long userId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.userId = userId;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
