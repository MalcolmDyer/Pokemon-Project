public class PokemonCharacter {
    private int hp;
    private int[] hpRange = new int[2];
    private int speed;
    private int[] speedRange = new int[2];
    private String name;
    private String japaneseName;

    public  void printPokemon() {
        System.out.println("Pokemon: " + name + " - " + japaneseName);
        System.out.println("Speed: " + speed);
        System.out.println("Hitpoints: " + hp);
        System.out.println();
    }
}
