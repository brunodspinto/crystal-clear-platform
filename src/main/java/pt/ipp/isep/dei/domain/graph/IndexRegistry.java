package pt.ipp.isep.dei.domain.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Maps entity ids (String) to a contiguous integer index used as a row/column
 * in an AdjacencyMatrix.
 * <p>
 * The first id seen gets index 0, the second gets 1, and so on. Asking for the
 * index of an id that was already registered returns the same index again.
 */
public class IndexRegistry {

    private final List<String> ids;

    /**
     * Instantiates a new Index registry.
     */
    public IndexRegistry() {
        this.ids = new ArrayList<>();
    }

    /**
     * Returns the index for the given id, registering it if needed.
     *
     * @param id the id
     * @return the int
     */
    public int indexFor(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        int i = ids.indexOf(id);
        if (i >= 0) {
            return i;
        }
        ids.add(id);
        return ids.size() - 1;
    }

    /**
     * Contains boolean.
     *
     * @param id the id
     * @return the boolean
     */
    public boolean contains(String id) {
        if (id == null) {
            return false;
        }
        return ids.indexOf(id) >= 0;
    }

    /**
     * Id at string.
     *
     * @param index the index
     * @return the string
     */
    public String idAt(int index) {
        if (index < 0 || index >= ids.size()) {
            throw new IndexOutOfBoundsException("index out of range: " + index);
        }
        return ids.get(index);
    }

    /**
     * Size int.
     *
     * @return the int
     */
    public int size() {
        return ids.size();
    }
}
