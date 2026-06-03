package pt.ipp.isep.dei.service;

/**
 * Email service implementation using the Gmail provider.
 */
public class GmailEmailService implements EmailService {

    @Override
    public void sendNotification(String toEmail, String subject, String body) {
        System.out.printf("[Gmail] To: %s | Subject: %s%n%s%n", toEmail, subject, body);
    }
}
