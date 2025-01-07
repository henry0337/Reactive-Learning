package dev.quochung2003.reactiveLearning.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MailService {
    JavaMailSender mailSender;

    @NonFinal
    @Value("spring.mail.username")
    String from;

    /**
     * Send an email to the specified {@code to}
     * @param to User that you want to send the email. Shouldn't be {@code null}.
     * @param bcc
     * @param cc
     * @param subject The email title. Shouldn't be {@code null}.
     * @param body The email body. Shouldn't be {@code null}.
     * @param replyTo User that you want to reply to. Can be {@code null}.
     */
    public void sendMail(
            final String[] to,
            final String[] bcc,
            final String[] cc,
            final String subject,
            String body,
            final String replyTo
    ) {
        final Date now = new Date();
        final var message = new SimpleMailMessage();
        message.setFrom(from);
        message.setSubject(subject);
        message.setText(body);
        message.setTo(to);
        message.setSentDate(now);
        message.setBcc(bcc);
        message.setCc(cc);
        message.setReplyTo(replyTo);

        mailSender.send(message);
    }
}
