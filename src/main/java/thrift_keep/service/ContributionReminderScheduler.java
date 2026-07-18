package thrift_keep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import thrift_keep.repository.MembershipRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ContributionReminderScheduler {

    private final MembershipRepository membershipRepository;
    private final NotificationService notificationService;

    // Runs every day at 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendReminders() {

        var memberships = membershipRepository.findByActiveTrue();

        memberships.forEach(membership -> {

            String frequency = membership.getGroup().getFrequency();

            if (shouldSendReminder(frequency)) {

                String message = "Reminder: Contribution due for group "
                        + membership.getGroup().getName();

                notificationService.createNotification(
                        membership.getUser(),
                        message
                );
            }
        });
    }

    private boolean shouldSendReminder(String frequency) {
        DayOfWeek today = LocalDate.now().getDayOfWeek();

        if ("WEEKLY".equalsIgnoreCase(frequency)) {
            return today == DayOfWeek.MONDAY
                    || today == DayOfWeek.WEDNESDAY
                    || today == DayOfWeek.FRIDAY;
        }

        if ("MONTHLY".equalsIgnoreCase(frequency)) {
            return LocalDate.now().getDayOfMonth() == 1;
        }

        return false;
    }
}