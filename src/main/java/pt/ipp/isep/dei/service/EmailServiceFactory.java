package pt.ipp.isep.dei.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Creates an EmailService based on the provider class configured in email.properties.
 */
public class EmailServiceFactory {

    private static final String CONFIG_FILE = "email.properties";
    private static final String CLASS_KEY = "email.provider.class";
    private static final String DEFAULT_CLASS = "pt.ipp.isep.dei.service.GmailEmailService";

    private EmailServiceFactory() {
    }

    /**
     * Returns the EmailService specified by email.properties.
     * Defaults to GmailEmailService if the file is missing or the class cannot be loaded.
     *
     * @return the configured EmailService
     */
    public static EmailService create() {
        String className = loadClassName();
        try {
            Class<?> cls = Class.forName(className);
            return (EmailService) cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return new GmailEmailService();
        }
    }

    private static String loadClassName() {
        Properties props = new Properties();
        InputStream in = EmailServiceFactory.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
        if (in == null) {
            return DEFAULT_CLASS;
        }
        try (InputStream stream = in) {
            props.load(stream);
        } catch (IOException e) {
            return DEFAULT_CLASS;
        }
        return props.getProperty(CLASS_KEY, DEFAULT_CLASS);
    }
}
