package pt.ipp.isep.dei.service;

/**
 * Email service implementation using the DEI internal provider.
 */
public class DeiEmailService implements EmailService {

    @Override
    public void sendNotification(String toEmail, String subject, String body) {
        System.out.printf("[DEI Service] To: %s | Subject: %s%n%s%n", toEmail, subject, body);
        NotificationLogger.log("DEI Service", toEmail, subject, body);
    }
}
