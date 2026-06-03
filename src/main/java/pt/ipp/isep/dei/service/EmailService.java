package pt.ipp.isep.dei.service;

/**
 * Sends email notifications to users.
 */
public interface EmailService {

    /**
     * Sends an email to the given address.
     *
     * @param toEmail the recipient's email address
     * @param subject the email subject
     * @param body    the email body
     */
    void sendNotification(String toEmail, String subject, String body);
}
