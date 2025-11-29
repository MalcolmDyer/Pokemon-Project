import java.util.ArrayList;

/**
 * Contract for loading raw Pokemon data from a source.
 */
public interface IReadData {
    /**
     * Attempts to open the data file.
     *
     * @param fileName path to the file
     * @return true if the file is found and ready for reading
     */
    boolean openDataFile(String fileName);

    /**
     * Reads all rows from the previously opened data file.
     *
     * @return true if at least one row was read
     */
    boolean readDataFile();

    /**
     * Returns a copy of the loaded rows.
     *
     * @return list of CSV rows
     */
    ArrayList<String> getRawDataList();
}
