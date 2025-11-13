import java.util.Comparator;

public class PokemonCharacter {
    private final int hp;
    private final int speed;
    private final String name;
    private final String japaneseName;

    public static final Comparator<PokemonCharacter> BY_HP_ASC =
            Comparator.comparingInt(PokemonCharacter::getHp)
                      .thenComparingInt(PokemonCharacter::getSpeed)
                      .thenComparing(PokemonCharacter::getName, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<PokemonCharacter> BY_SPEED_ASC =
            Comparator.comparingInt(PokemonCharacter::getSpeed)
                      .thenComparingInt(PokemonCharacter::getHp)
                      .thenComparing(PokemonCharacter::getName, String.CASE_INSENSITIVE_ORDER);

    public PokemonCharacter(String name, String japaneseName, int hp, int speed) {
        this.name = name;
        this.japaneseName = japaneseName;
        this.hp = hp;
        this.speed = speed;
    }

    public int getHp() {
        return hp;
    }

    public int getSpeed() {
        return speed;
    }

    public String getName() {
        return name;
    }

    public String getJapaneseName() {
        return japaneseName;
    }

    public String formatDetails() {
        String jp = japaneseName == null || japaneseName.isEmpty() ? "N/A" : japaneseName;
        return name + " / " + jp + " | HP: " + hp + " | Speed: " + speed;
    }
}
