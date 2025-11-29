import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Reads raw CSV data from a file into memory.
 */
public class ReadData implements IReadData {
    private final ArrayList<String> rawData = new ArrayList<>();
    private String currentFileName;

    /**
     * Creates a new reader.
     */
    public ReadData() {
        // Default constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean openDataFile(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return false;
        }

        File file = new File(fileName.trim());
        if (!file.exists() || !file.isFile()) {
            currentFileName = null;
            return false;
        }

        currentFileName = file.getPath();
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean readDataFile() {
        if (currentFileName == null) {
            return false;
        }

        rawData.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(currentFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                rawData.add(line);
            }
            return !rawData.isEmpty();
        } catch (IOException e) {
            rawData.clear();
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ArrayList<String> getRawDataList() {
        return new ArrayList<>(rawData);
    }
}
