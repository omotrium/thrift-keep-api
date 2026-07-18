package thrift_keep.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private String memberName;
    private String memberEmail;
    private int cycleNumber;
    private String status;
    private Double amount;
    private LocalDate dueDate;
    private boolean isOwner;

    public PaymentResponse(Long id, String memberName, String memberEmail, int cycleNumber, String status, Double amount, LocalDate dueDate, boolean isOwner) {
        this.id = id;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.cycleNumber = cycleNumber;
        this.status = status;
        this.amount = amount;
        this.dueDate = dueDate;
        this.isOwner = isOwner;
    }

    public PaymentResponse(Payment p) {
        this.id = p.getId();
        this.memberName = p.getMembership().getUser().getName();
        this.memberEmail = p.getMembership().getUser().getEmail();
        this.amount = p.getAmount();
        this.status = p.getStatus().name();
        this.dueDate = p.getDueDate();
    }
}
