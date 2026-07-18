package thrift_keep.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {


    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("Your Password Reset Code");

        message.setText(
                "Your OTP code is: " + otp + "\n\n" +
                        "This code expires in 10 minutes.\n\n" +
                        "If you didn’t request this, ignore this email."
        );

        System.out.println("++++++++++++++++++++++++"+ message.toString());

        mailSender.send(message);
    }
}
