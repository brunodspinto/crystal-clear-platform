package pt.ipp.isep.dei.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Handles the persistence of the {@link Repositories} singleton to a binary file
 * using Java object serialization, so the application data survives between two
 * successive runs (non-functional requirement).
 *
 * <p>It follows the pattern taught in PPROG (object serialization to a binary
 * file with {@link ObjectOutputStream} / {@link ObjectInputStream}): {@link #save}
 * writes the object graph and {@link #load} reads it back, both failing
 * gracefully (a missing or unreadable file simply yields no persisted data).</p>
 */
public class RepositoriesFile {

    /**
     * Default name of the binary file where the repositories are persisted.
     */
    public static final String DEFAULT_FILE_NAME = "repositories.dat";

    /**
     * Loads the repositories from the default file.
     *
     * @return the persisted {@link Repositories}, or {@code null} if the file does
     *         not exist or cannot be read.
     */
    public Repositories load() {
        return load(DEFAULT_FILE_NAME);
    }

    /**
     * Loads the repositories from the file with the given name.
     *
     * @param fileName the name of the file to read.
     * @return the persisted {@link Repositories}, or {@code null} if it cannot be read.
     */
    public Repositories load(String fileName) {
        return load(new File(fileName));
    }

    /**
     * Loads the repositories from the given file.
     *
     * @param file the file to read.
     * @return the persisted {@link Repositories}, or {@code null} if it cannot be read.
     */
    public Repositories load(File file) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (Repositories) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            return null;
        }
    }

    /**
     * Saves the repositories to the default file.
     *
     * @param repositories the repositories to persist.
     * @return {@code true} if saved successfully, {@code false} on I/O error.
     */
    public boolean save(Repositories repositories) {
        return save(DEFAULT_FILE_NAME, repositories);
    }

    /**
     * Saves the repositories to the file with the given name.
     *
     * @param fileName     the name of the file to write.
     * @param repositories the repositories to persist.
     * @return {@code true} if saved successfully, {@code false} on I/O error.
     */
    public boolean save(String fileName, Repositories repositories) {
        return save(new File(fileName), repositories);
    }

    /**
     * Saves the repositories to the given file.
     *
     * @param file         the file to write.
     * @param repositories the repositories to persist.
     * @return {@code true} if saved successfully, {@code false} on I/O error.
     */
    public boolean save(File file, Repositories repositories) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(repositories);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
