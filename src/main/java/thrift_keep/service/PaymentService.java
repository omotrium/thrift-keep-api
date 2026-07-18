package thrift_keep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thrift_keep.exception.CustomiseException;
import thrift_keep.model.*;
import thrift_keep.repository.ActivityRepository;
import thrift_keep.repository.GroupRepository;
import thrift_keep.repository.PaymentRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ActivityRepository activityRepository;
    private final NotificationService notificationService;
    private final GroupRepository groupRepository;
    private final MyWebSocketHandler webSocketHandler;
    private final PushNotification pushNotification;

    // Member marks as paid
    @Transactional
    public PaymentResponse submitPayment(Long paymentId, String email) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomiseException("Payment not found"));

        // Only owner of payment can submit
        if (!payment.getMembership().getUser().getEmail().equals(email)) {
            throw new CustomiseException("You cannot submit someone else's payment");
        }

        if (!payment.getMembership().getActive()) {
            throw new CustomiseException("Membership is no longer active");
        }

        // Only allow submit if still pending
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new CustomiseException("Payment already submitted or approved");
        }

        // Update status
        payment.setStatus(PaymentStatus.SUBMITTED);

        // Track submission time (optional but better)
        payment.setSubmittedAt(LocalDate.now());

        String label = payment.getGroup().getFrequency().equalsIgnoreCase("WEEKLY")
                ? "Week "
                : "Month ";

        activityRepository.save(
                new Activity(
                        payment.getGroup(),

                        payment.getMembership().getUser(),
                        "PAYMENT_SUBMITTED",
                        "Payment submitted for " + label + payment.getCycleNumber(),
                        payment.getCycleNumber()
                )
        );

        return new PaymentResponse(paymentRepository.save(payment));
    }

    // Admin approves payment
    @Transactional
    public PaymentResponse approvePayment(Long paymentId, String email) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomiseException("Payment not found"));

        // Only owner can approve
        if (!payment.getGroup().getOwner().getEmail().equals(email)) {
            throw new CustomiseException("You can't approve this payment");
        }

        // Only allow approval if already submitted
        if (payment.getStatus() != PaymentStatus.SUBMITTED) {
            throw new CustomiseException("Payment must be submitted before approval");
        }

        payment.setStatus(PaymentStatus.APPROVED);
        payment.setApprovedAt(LocalDate.now());

        activityRepository.save(
                new Activity(
                        payment.getGroup(),
                        payment.getMembership().getUser(),
                        "PAYMENT_APPROVED",
                        "Payment approved for Week " + payment.getCycleNumber(),
                        payment.getCycleNumber()
                )
        );

        return new PaymentResponse(paymentRepository.save(payment));
    }

    public List<PaymentResponse> getMyPayments(Long groupId, String email) {

        List<Payment> payments =
                paymentRepository.findPaymentsForUser(groupId, email);

        return payments.stream()
                .map(p -> new PaymentResponse(
                        p.getId(),
                        p.getMembership().getUser().getName(),
                        p.getMembership().getUser().getEmail(),
                        p.getCycleNumber(),
                        p.getStatus().name(),
                        p.getAmount(),
                        p.getDueDate(),
                        p.getGroup().getOwner().getEmail().equals(email)
                ))
                .toList();
    }


    public int calculateCurrentCycle(GroupEntity group) {

        LocalDate start = group.getStartDate();

        if (start == null) {
            return 1; //
        }
        LocalDate today = LocalDate.now();
        if (group.getFrequency().equalsIgnoreCase("WEEKLY")) {
            return (int) ChronoUnit.WEEKS.between(start, today) + 1;
        } else {
            return (int) ChronoUnit.MONTHS.between(start, today) + 1;
        }
    }

    public void sendPaymentReminders() {

        LocalDate today = LocalDate.now();

        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);


        List<Payment> current =
                paymentRepository.findPendingPaymentsForWeek(startOfWeek, endOfWeek, today);

        List<Payment> overdue =
                paymentRepository.findOverduePendingPayments(startOfWeek, today);

        List<Payment> all = new ArrayList<>();
        all.addAll(current);
        all.addAll(overdue);

        Map<UserAccount, List<Payment>> grouped =
                all.stream()
                        .collect(Collectors.groupingBy(p -> p.getMembership().getUser()));

        grouped.forEach((user, payments) -> {

            String message =
                    "You have " + payments.size() +
                            " pending contribution(s).";

            notificationService.sendPush(
                    user.getPushToken(),
                    "Payment Reminder",
                    message
            );
        });
    }
}
