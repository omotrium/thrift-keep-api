package thrift_keep.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import thrift_keep.model.PaymentResponse;
import thrift_keep.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Member submits payment
    @PutMapping("/{paymentId}/submit")
    public PaymentResponse submitPayment(
            @PathVariable Long paymentId,
            Authentication auth) {
        return paymentService.submitPayment(paymentId, auth.getName());
    }

    // Admin approves
    @PutMapping("/{paymentId}/approve")
    public PaymentResponse approvePayment(
            @PathVariable Long paymentId,
            Authentication auth
    ) {
        return paymentService.approvePayment(paymentId,auth.getName());
    }

    @GetMapping("/group/{groupId}")
    public List<PaymentResponse> getMyPayments(@PathVariable Long groupId, Authentication auth) {
        return paymentService.getMyPayments(groupId, auth.getName());
    }

}
