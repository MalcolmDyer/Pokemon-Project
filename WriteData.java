import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class WriteData implements IWriteData {
    @Override
    public boolean writeDataToFile(HashSet<String> someData, String fileName) {
        if (someData == null || someData.isEmpty() || fileName == null || fileName.isBlank()) {
            return false;
        }

        List<String> orderedData = new ArrayList<>(someData);
        Collections.sort(orderedData, String.CASE_INSENSITIVE_ORDER);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String entry : orderedData) {
                writer.write(entry);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
