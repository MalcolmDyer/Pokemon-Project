import java.util.ArrayList;
import java.util.HashSet;

public class TestData {
    private static final int LINES_TO_PRINT = 7;
    private final IWriteData writeData;

    public TestData(IWriteData writeData) {
        this.writeData = writeData;
    }

    public void printFirstAndLastSeven(ArrayList<String> rawData) {
        if (rawData == null || rawData.isEmpty()) {
            System.out.println("No data available. Load the file first.");
            return;
        }

        int linesToShow = Math.min(LINES_TO_PRINT, rawData.size());
        System.out.println("First " + linesToShow + " lines:");
        for (int i = 0; i < linesToShow; i++) {
            System.out.println(rawData.get(i));
        }

        System.out.println("Last " + linesToShow + " lines:");
        int startIndex = Math.max(rawData.size() - LINES_TO_PRINT, 0);
        for (int i = startIndex; i < rawData.size(); i++) {
            System.out.println(rawData.get(i));
        }
    }

    public boolean writeCharacterNames(HashSet<String> characterNames, String fileName) {
        if (characterNames == null || characterNames.isEmpty()) {
            System.out.println("There are no character names to write.");
            return false;
        }

        boolean result = writeData.writeDataToFile(characterNames, fileName);
        if (result) {
            System.out.println("Character names written to " + fileName);
        } else {
            System.out.println("Failed to write character names to " + fileName);
        }
        return result;
    }
}
