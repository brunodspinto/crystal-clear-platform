package pt.ipp.isep.dei.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Creates an EmailService based on the provider configured in email.properties.
 */
public class EmailServiceFactory {

    private static final String CONFIG_FILE = "email.properties";
    private static final String PROVIDER_KEY = "email.provider";

    private EmailServiceFactory() {
    }

    /**
     * Returns the EmailService specified by email.properties.
     * Defaults to GmailEmailService if the file is missing or the provider is unrecognised.
     *
     * @return the configured EmailService
     */
    public static EmailService create() {
        String provider = loadProvider();
        if ("dei".equalsIgnoreCase(provider)) {
            return new DeiEmailService();
        }
        return new GmailEmailService();
    }

    private static String loadProvider() {
        Properties props = new Properties();
        InputStream in = EmailServiceFactory.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (in == null) {
            return "gmail";
        }
        try (InputStream stream = in) {
            props.load(stream);
        } catch (IOException e) {
            return "gmail";
        }
        return props.getProperty(PROVIDER_KEY, "gmail");
    }
}
