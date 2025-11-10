import java.util.ArrayList;
import java.util.List;

/**
 * Utility helpers for parsing the Pokemon CSV file.
 */
public final class PokemonCsvUtils {
    private PokemonCsvUtils() {
        // Utility class
    }

    /**
     * Splits a CSV record while respecting quoted fields.
     *
     * @param record raw CSV record
     * @return ordered list of columns (quotes trimmed)
     */
    public static List<String> splitCsvRecord(String record) {
        List<String> tokens = new ArrayList<>();
        if (record == null) {
            return tokens;
        }

        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < record.length(); i++) {
            char c = record.charAt(i);

            if (c == '"') {
                insideQuotes = !insideQuotes;
                continue;
            }

            if (c == ',' && !insideQuotes) {
                tokens.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        tokens.add(current.toString().trim());
        return tokens;
    }

    /**
     * Finds the column index for a header name.
     *
     * @param headerLine header row from the CSV
     * @param columnName column to search for
     * @return zero-based column index, or -1 if not found
     */
    public static int findColumnIndex(String headerLine, String columnName) {
        if (headerLine == null || columnName == null) {
            return -1;
        }

        List<String> headers = splitCsvRecord(headerLine);
        for (int i = 0; i < headers.size(); i++) {
            if (columnName.equalsIgnoreCase(headers.get(i))) {
                return i;
            }
        }

        return -1;
    }
}
