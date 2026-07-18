package thrift_keep.config;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import thrift_keep.service.PaymentService;

@Component
@RequiredArgsConstructor
public class PaymentReminderScheduler {

    private final PaymentService paymentService;

    @Scheduled(cron = "0 0 8 * * MON")
    public void sendWeeklyReminders() {
        paymentService.sendPaymentReminders();
    }
}
