import java.util.HashSet;

/**
 * Contract for persisting processed data.
 */
public interface IWriteData {
    /**
     * Writes a set of strings to the provided file.
     *
     * @param someData entries to write
     * @param fileName target file name
     * @return true on success
     */
    boolean writeDataToFile(HashSet<String> someData, String fileName);
}
