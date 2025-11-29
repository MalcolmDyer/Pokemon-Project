import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Performs data analysis tasks on Pokemon CSV rows.
 */
public class AnalyzePokemonData implements IAnalyzePokemonData {
    private static final String NAME_COLUMN = "name";

    /**
     * Creates a new analyzer instance.
     */
    public AnalyzePokemonData() {
        // Default constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HashSet<String> getAllCharacterNames(ArrayList<String> originalData) {
        HashSet<String> characterNames = new HashSet<>();

        if (originalData == null || originalData.isEmpty()) {
            return characterNames;
        }

        int nameIndex = PokemonCsvUtils.findColumnIndex(originalData.get(0), NAME_COLUMN);
        if (nameIndex < 0) {
            return characterNames;
        }

        for (int i = 1; i < originalData.size(); i++) {
            List<String> row = PokemonCsvUtils.splitCsvRecord(originalData.get(i));
            if (nameIndex < row.size()) {
                String name = row.get(nameIndex).trim();
                if (!name.isEmpty()) {
                    characterNames.add(name);
                }
            }
        }

        return characterNames;
    }
}
