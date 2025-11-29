import java.util.Comparator;

/**
 * Immutable representation of a Pokemon with basic battle stats.
 */
public class PokemonCharacter {
    private final int hp;
    private final int speed;
    private final String name;
    private final String japaneseName;

    /**
     * Sorts characters by HP (ascending), then speed, then name.
     */
    public static final Comparator<PokemonCharacter> BY_HP_ASC =
            Comparator.comparingInt(PokemonCharacter::getHp)
                      .thenComparingInt(PokemonCharacter::getSpeed)
                      .thenComparing(PokemonCharacter::getName, String.CASE_INSENSITIVE_ORDER);

    /**
     * Sorts characters by speed (ascending), then HP, then name.
     */
    public static final Comparator<PokemonCharacter> BY_SPEED_ASC =
            Comparator.comparingInt(PokemonCharacter::getSpeed)
                      .thenComparingInt(PokemonCharacter::getHp)
                      .thenComparing(PokemonCharacter::getName, String.CASE_INSENSITIVE_ORDER);

    /**
     * Creates a new PokemonCharacter.
     *
     * @param name          English name
     * @param japaneseName  Japanese name (may be blank)
     * @param hp            hit points
     * @param speed         speed value
     */
    public PokemonCharacter(String name, String japaneseName, int hp, int speed) {
        this.name = name;
        this.japaneseName = japaneseName;
        this.hp = hp;
        this.speed = speed;
    }

    /**
     * Returns the hit point value.
     *
     * @return hp
     */
    public int getHp() {
        return hp;
    }

    /**
     * Returns the speed value.
     *
     * @return speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Returns the English name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the Japanese name, which may be blank.
     *
     * @return Japanese name
     */
    public String getJapaneseName() {
        return japaneseName;
    }

    /**
     * Formats a compact summary of this character.
     *
     * @return printable details string
     */
    public String formatDetails() {
        String jp = japaneseName == null || japaneseName.isEmpty() ? "N/A" : japaneseName;
        return name + " / " + jp + " | HP: " + hp + " | Speed: " + speed;
    }
}
