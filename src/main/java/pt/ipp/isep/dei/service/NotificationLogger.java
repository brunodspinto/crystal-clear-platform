package pt.ipp.isep.dei.service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Writes every sent notification to a local file (notifications.txt), so the
 * accepted/rejected registration emails are kept as a record and can be shown
 * as proof that the user was notified (US02 AC1/AC2).
 *
 * The message is built with a StringBuilder and appended to the end of the
 * file, so previous notifications are never lost.
 */
public class NotificationLogger {

    private static final String FILE_NAME = "notifications.txt";

    private NotificationLogger() {}

    /**
     * Appends a notification to the log file.
     *
     * @param provider the name of the email provider used.
     * @param toEmail  the destination email address.
     * @param subject  the subject of the notification.
     * @param body     the body of the notification.
     */
    public static void log(String provider, String toEmail, String subject, String body) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(new Date()).append("] ");
        sb.append("[").append(provider).append("] ");
        sb.append("To: ").append(toEmail).append("\n");
        sb.append("Subject: ").append(subject).append("\n");
        sb.append(body).append("\n");
        sb.append("------------------------------------------------------------\n");

        try {
            FileWriter writer = new FileWriter(FILE_NAME, true);
            writer.append(sb.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not write notification to file: " + e.getMessage());
        }
    }
}
