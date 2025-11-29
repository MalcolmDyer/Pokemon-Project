import java.util.ArrayList;
import java.util.HashSet;

/**
 * Contract for analyzing raw Pokemon data.
 */
public interface IAnalyzePokemonData {
    /**
     * Collects all character names found in the provided data set.
     *
     * @param originalData raw CSV rows including the header
     * @return unique set of character names (may be empty)
     */
    HashSet<String> getAllCharacterNames(ArrayList<String> originalData);
}
