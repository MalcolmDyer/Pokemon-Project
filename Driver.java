import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

public class Driver {
    private static final Scanner SCANNER = new Scanner(System.in);

    // ==== Main menu ====
    private static final int MENU_EXIT = 1;
    private static final int MENU_OPEN_FILE = 2;
    private static final int MENU_SEARCH_BY_NAME = 3;
    private static final int MENU_SEARCH_BY_ATTRIBUTES = 4;
    private static final int MENU_UNIT_TEST = 10;

    // ==== Sub menus ====
    private static final int SUB_MENU_BACK = 0;

    // ==== Sub menu 1
    private static final int SUB_MENU1_PRINT_LINES = 1;
    private static final int SUB_MENU1_WRITE_NAMES = 2;

    // ==== Sub menu 2 - attribute search
    private static final int SUB_MENU2_SEARCH_BY_HITPOINTS = 1;
    private static final int SUB_MENU2_SEARCH_BY_SPEED = 2;

    // ==== Sub menu 3 - attribute: HP
    private static final int SUB_MENU3_FIND_SPECIFIC_HP = 1;
    private static final int SUB_MENU3_FIND_HP_RANGE = 2;
    private static final int SUB_MENU3_FIND_LOWEST_HP = 3;
    private static final int SUB_MENU3_FIND_HIGHEST_HP = 4;
    private static final int SUB_MENU3_GO_BACK = 5;
    private static final String SUB_MENU3_OPTION_PATTERN = "[1-5]";

    // ==== sub menu 4 - attribute: speed
    private static final int SUB_MENU4_FASTEST_SPEED = 1;
    private static final int SUB_MENU4_SLOWEST_SPEED = 2;
    private static final int SUB_MENU4_TOP3_FASTEST = 3;
    private static final int SUB_MENU4_BOTTOM3_SLOWEST = 4;
    private static final int SUB_MENU4_SPEED_RANGE = 5;
    private static final int SUB_MENU4_TOP3_GROUPS = 6;
    private static final int SUB_MENU4_LARGEST_GROUP = 7;
    private static final int SUB_MENU4_GO_BACK = 8;
    private static final String SUB_MENU4_OPTION_PATTERN = "[1-8]";

    // ==== other constants ====
    private static final String DEFAULT_FILE = "pokemon.csv";
    private static final String RESULTS_FILE = "character_names.txt";
    private static final int MAX_FILENAME_ATTEMPTS = 2;
    private static final String NAME_COLUMN = "name";
    private static final String JAPANESE_NAME_COLUMN = "japanese_name";
    private static final String SPEED_COLUMN = "speed";
    private static final String HP_COLUMN = "hp";

    private static final IReadData DATA_READER = new ReadData();
    private static final IAnalyzePokemonData DATA_ANALYZER = new AnalyzePokemonData();
    private static final TestData TEST_DATA = new TestData(new WriteData());

    private static ArrayList<String> rawData = new ArrayList<>();
    private static HashSet<String> cachedNames = new HashSet<>();
    private static ArrayList<String> sortedNames = new ArrayList<>();
    private static final Map<String, String> NAME_TO_ROW = new HashMap<>();
    private static final Map<String, PokemonCharacter> CHARACTER_BY_NAME = new HashMap<>();
    private static final List<PokemonCharacter> CHARACTERS = new ArrayList<>();
    private static int minHpValue = Integer.MAX_VALUE;
    private static int maxHpValue = Integer.MIN_VALUE;
    private static int minSpeedValue = Integer.MAX_VALUE;
    private static int maxSpeedValue = Integer.MIN_VALUE;

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            int option = showMenu();

            switch (option) {
                case MENU_EXIT:
                    System.out.println("Thanks for using this application.");
                    running = false;
                    break;
                case MENU_OPEN_FILE:
                    handleOpenAndRead();
                    break;
                case MENU_SEARCH_BY_NAME:
                    handleSearchByName();
                    break;
                case MENU_SEARCH_BY_ATTRIBUTES:
                    handleSearchByAttributes();
                    break;
                case MENU_UNIT_TEST:
                    handleUnitTests();
                    break;
                default:
                    System.out.println("Unsupported option. Try again.");
            }
        }
    }

    private static int showMenu() {
        while (true) {
            System.out.println();
            System.out.println("Pokemon Data Menu");
            System.out.println(MENU_EXIT + " - Exit");
            System.out.println(MENU_OPEN_FILE + " - Open and read data file");
            System.out.println(MENU_SEARCH_BY_NAME + " - Search for a character by name");
            System.out.println(MENU_SEARCH_BY_ATTRIBUTES + " - Search for a character by attributes");
            System.out.println(MENU_UNIT_TEST + " - Unit Testing");
            System.out.print("Enter an option: ");

            String choice = SCANNER.nextLine().trim();

            if (choice.matches("1|2|3|4|10")) {
                return Integer.parseInt(choice);
            }

            System.out.println("Please enter 1, 2, 3, 4, or 10.");
        }
    }

    private static void handleOpenAndRead() {
        for (int attempt = 1; attempt <= MAX_FILENAME_ATTEMPTS; attempt++) {
            System.out.print("Enter data file name (default " + DEFAULT_FILE + "): ");
            String fileName = SCANNER.nextLine().trim();

            if (fileName.isEmpty()) {
                fileName = DEFAULT_FILE;
            }

            if (!DATA_READER.openDataFile(fileName)) {
                System.out.println("Could not find the file: " + fileName);
                continue;
            }

            if (!DATA_READER.readDataFile()) {
                System.out.println("Failed to read the file: " + fileName);
                continue;
            }

            rawData = DATA_READER.getRawDataList();
            cachedNames = DATA_ANALYZER.getAllCharacterNames(rawData);
            sortedNames = new ArrayList<>(cachedNames);
            Collections.sort(sortedNames, String.CASE_INSENSITIVE_ORDER);   // alphabetizes list allowing for binary seach for "Search for a character" menu option
            buildSearchIndex();
            buildPokemonCharacters();
            System.out.println("Successfully loaded " + rawData.size() + " rows.");
            return;
        }

        rawData.clear();
        cachedNames.clear();
        sortedNames.clear();
        NAME_TO_ROW.clear();
        resetCharacterCollections();
        System.out.println("Unable to open the file after " + MAX_FILENAME_ATTEMPTS + " attempts.");
    }

    private static void handleUnitTests() {
        if (rawData.isEmpty()) {
            System.out.println("Load data before running tests.");
            return;
        }

        int subOption = showSubMenu1();
        switch (subOption) {
            case SUB_MENU_BACK:
                return;
            case SUB_MENU1_PRINT_LINES:
                TEST_DATA.printFirstAndLastSeven(rawData);
                break;
            case SUB_MENU1_WRITE_NAMES:
                HashSet<String> names = DATA_ANALYZER.getAllCharacterNames(rawData);
                TEST_DATA.writeCharacterNames(names, RESULTS_FILE);
                break;
            default:
                System.out.println("Unknown sub-option selected.");
        }
    }

    private static int showSubMenu1() {
        while (true) {
            System.out.println();
            System.out.println("Unit Test Menu");
            System.out.println(SUB_MENU_BACK + " - Return to main menu");
            System.out.println(SUB_MENU1_PRINT_LINES + " - Print first 7 and last 7 lines");
            System.out.println(SUB_MENU1_WRITE_NAMES + " - Write character names to file");
            System.out.print("Enter an option: ");

            String choice = SCANNER.nextLine().trim();
            if (choice.matches("0|1|2")) {
                return Integer.parseInt(choice);
            }

            System.out.println("Please enter 0, 1, or 2.");
        }
    }

    private static void handleSearchByName() {
        if (sortedNames.isEmpty() || NAME_TO_ROW.isEmpty()) {
            System.out.println("Load data before searching.");
            return;
        }

        System.out.print("Enter character name to search: ");
        String query = SCANNER.nextLine().trim();
        if (query.isEmpty()) {
            System.out.println("Search term cannot be empty.");
            return;
        }

        int index = Collections.binarySearch(sortedNames, query, String.CASE_INSENSITIVE_ORDER);
        if (index < 0) {
            System.out.println("Could not find " + query + " in the dataset.");
            return;
        }

        String matchedName = sortedNames.get(index);
        String row = NAME_TO_ROW.get(matchedName.toLowerCase());
        if (row == null) {
            System.out.println("Row data missing for " + matchedName + ".");
            return;
        }

        System.out.println(rawData.get(0));
        System.out.println(row);
    }


    //      ==== Attribute Search ====
    private static int showSubMenu2() {
        while (true) {
            System.out.println();
            System.out.println("Search by Attributes Menu");
            System.out.println(SUB_MENU_BACK + " - Return to main menu");
            System.out.println(SUB_MENU2_SEARCH_BY_HITPOINTS + " - Search by number of hitpoints");
            System.out.println(SUB_MENU2_SEARCH_BY_SPEED + " - Search by speed");
            System.out.print("Enter an option: ");

            String choice = SCANNER.nextLine().trim();
            if (choice.matches("0|1|2")) {
                return Integer.parseInt(choice);
            }

            System.out.println("Please enter 0, 1, or 2.");
        }
    }

    //      ==== Attribute menu: HP ====
    private static int showSubMenu3() {
        while (true) {
            System.out.println();
            System.out.println("Search by Hit Points");
            System.out.println(SUB_MENU3_FIND_SPECIFIC_HP + " - Find a character with a specific hit point value");
            System.out.println(SUB_MENU3_FIND_HP_RANGE + " - Find characters within a specific range of hit values");
            System.out.println(SUB_MENU3_FIND_LOWEST_HP + " - Find the character with the lowest hit point value");
            System.out.println(SUB_MENU3_FIND_HIGHEST_HP + " - Find the character with the highest hit point value");
            System.out.println(SUB_MENU3_GO_BACK + " - Go back to the previous menu");
            System.out.print("Enter an option: ");

            String choice = SCANNER.nextLine().trim();
            if (choice.matches(SUB_MENU3_OPTION_PATTERN)) {
                return Integer.parseInt(choice);
            }

            System.out.println("Please enter a number between 1 and 5.");
        }
    }

    //      ==== Attribute menu: Speed ====
    private static int showSubMenu4() {
        while (true) {
            System.out.println();
            System.out.println("Search by Speed");
            System.out.println(SUB_MENU4_FASTEST_SPEED + " - Which character has the fastest speed");
            System.out.println(SUB_MENU4_SLOWEST_SPEED + " - Which character has the slowest speed");
            System.out.println(SUB_MENU4_TOP3_FASTEST + " - Which characters are part of the top 3 fastest speeds");
            System.out.println(SUB_MENU4_BOTTOM3_SLOWEST + " - Which characters are part of the 3 slowest speeds");
            System.out.println(SUB_MENU4_SPEED_RANGE + " - Which characters are part of a specific range of speeds");
            System.out.println(SUB_MENU4_TOP3_GROUPS + " - What are the top 3 speed groups, and what is the list of characters which are part of each speed group");
            System.out.println(SUB_MENU4_LARGEST_GROUP + " - Which group of characters represent the largest speed group");
            System.out.println(SUB_MENU4_GO_BACK + " - Go back to the previous menu");
            System.out.print("Enter an option: ");

            String choice = SCANNER.nextLine().trim();
            if (choice.matches(SUB_MENU4_OPTION_PATTERN)) {
                return Integer.parseInt(choice);
            }

            System.out.println("Please enter a number between 1 and 8.");
        }
    }


    public static void handleSearchByAttributes() {
        if (!hasCharacterData()) {
            return;
        }

        boolean keepGoing = true;
        while (keepGoing) {
            int subOption = showSubMenu2();
            switch (subOption) {
                case SUB_MENU_BACK:
                    keepGoing = false;
                    break;
                case SUB_MENU2_SEARCH_BY_HITPOINTS:
                    handleSearchByHP();
                    break;
                case SUB_MENU2_SEARCH_BY_SPEED:
                    handleSearchBySpeed();
                    break;
                default:
                    System.out.println("Unknown sub-option selected.");
            }
        }
    }

    private static void handleSearchByHP() {
        if (!hasCharacterData()) {
            return;
        }

        boolean keepGoing = true;
        while (keepGoing) {
            int subOption = showSubMenu3();
            switch (subOption) {
                case SUB_MENU3_FIND_SPECIFIC_HP:
                    handleSpecificHpSearch();
                    break;
                case SUB_MENU3_FIND_HP_RANGE:
                    handleHpRangeSearch();
                    break;
                case SUB_MENU3_FIND_LOWEST_HP:
                    handleExtremumHpSearch(true);
                    break;
                case SUB_MENU3_FIND_HIGHEST_HP:
                    handleExtremumHpSearch(false);
                    break;
                case SUB_MENU3_GO_BACK:
                    keepGoing = false;
                    break;
                default:
                    System.out.println("Unknown sub-option selected.");
            }
        }
    }

    private static void handleSpecificHpSearch() {
        displayRange("HP", minHpValue, maxHpValue);
        int targetHp = promptForIntWithinRange("Enter the HP value to search for: ", minHpValue, maxHpValue);
        TreeSet<PokemonCharacter> matches = collectCharactersByHpRange(targetHp, targetHp);
        if (matches.isEmpty()) {
            System.out.println("No characters found with HP value " + targetHp + ".");
            return;
        }

        System.out.println("Characters with HP " + targetHp + " (" + matches.size() + "):");
        printCharacterDetails(matches);
    }

    private static void handleHpRangeSearch() {
        displayRange("HP", minHpValue, maxHpValue);
        int minHp = promptForIntWithinRange("Enter minimum HP (inclusive): ", minHpValue, maxHpValue);
        int maxHp = promptForIntWithinRange("Enter maximum HP (inclusive): ", minHpValue, maxHpValue);
        if (minHp > maxHp) {
            int temp = minHp;
            minHp = maxHp;
            maxHp = temp;
        }

        TreeSet<PokemonCharacter> matches = collectCharactersByHpRange(minHp, maxHp);
        if (matches.isEmpty()) {
            System.out.println("No characters found within the HP range " + minHp + "-" + maxHp + ".");
            return;
        }

        System.out.println("Characters with HP between " + minHp + " and " + maxHp + " (" + matches.size() + "):");
        printCharacterDetails(matches);
    }

    private static void handleExtremumHpSearch(boolean findLowest) {
        int targetHp = findLowest ? minHpValue : maxHpValue;
        TreeSet<PokemonCharacter> matches = collectCharactersByHpRange(targetHp, targetHp);
        if (matches.isEmpty()) {
            System.out.println("No HP data available.");
            return;
        }

        String descriptor = findLowest ? "Lowest" : "Highest";
        System.out.println(descriptor + " HP value: " + targetHp);
        printCharacterDetails(matches);
    }

    private static void handleSearchBySpeed() {
        if (!hasCharacterData()) {
            return;
        }

        boolean keepGoing = true;
        while (keepGoing) {
            int subOption = showSubMenu4();
            switch (subOption) {
                case SUB_MENU4_FASTEST_SPEED:
                    handleExtremumSpeedSearch(false);
                    break;
                case SUB_MENU4_SLOWEST_SPEED:
                    handleExtremumSpeedSearch(true);
                    break;
                case SUB_MENU4_TOP3_FASTEST:
                    handleTopSpeedValues(true);
                    break;
                case SUB_MENU4_BOTTOM3_SLOWEST:
                    handleTopSpeedValues(false);
                    break;
                case SUB_MENU4_SPEED_RANGE:
                    handleSpeedRangeSearch();
                    break;
                case SUB_MENU4_TOP3_GROUPS:
                    handleTopSpeedGroupsBySize();
                    break;
                case SUB_MENU4_LARGEST_GROUP:
                    handleLargestSpeedGroup();
                    break;
                case SUB_MENU4_GO_BACK:
                    keepGoing = false;
                    break;
                default:
                    System.out.println("Unknown sub-option selected.");
            }
        }
    }

    private static void handleSpeedRangeSearch() {
        displayRange("Speed", minSpeedValue, maxSpeedValue);
        int minSpeed = promptForIntWithinRange("Enter minimum speed (inclusive): ", minSpeedValue, maxSpeedValue);
        int maxSpeed = promptForIntWithinRange("Enter maximum speed (inclusive): ", minSpeedValue, maxSpeedValue);
        if (minSpeed > maxSpeed) {
            int temp = minSpeed;
            minSpeed = maxSpeed;
            maxSpeed = temp;
        }

        TreeSet<PokemonCharacter> matches = collectCharactersBySpeedRange(minSpeed, maxSpeed);
        if (matches.isEmpty()) {
            System.out.println("No characters found within the speed range " + minSpeed + "-" + maxSpeed + ".");
            return;
        }

        System.out.println("Characters with speed between " + minSpeed + " and " + maxSpeed + " (" + matches.size() + "):");
        printCharacterDetails(matches);
    }

    private static void handleExtremumSpeedSearch(boolean findLowest) {
        int targetSpeed = findLowest ? minSpeedValue : maxSpeedValue;
        TreeSet<PokemonCharacter> matches = collectCharactersBySpeedRange(targetSpeed, targetSpeed);
        if (matches.isEmpty()) {
            System.out.println("No speed data available.");
            return;
        }

        String descriptor = findLowest ? "Slowest" : "Fastest";
        System.out.println(descriptor + " speed value: " + targetSpeed);
        printCharacterDetails(matches);
    }

    private static void handleTopSpeedValues(boolean fastest) {
        Map<Integer, TreeSet<PokemonCharacter>> groups = buildSpeedBuckets();
        if (groups.isEmpty()) {
            System.out.println("No speed data available.");
            return;
        }

        ArrayList<Integer> speeds = new ArrayList<>(groups.keySet());
        if (fastest) {
            speeds.sort(Collections.reverseOrder());
        } else {
            Collections.sort(speeds);
        }

        int limit = Math.min(3, speeds.size());
        String descriptor = fastest ? "fastest" : "slowest";
        String qualifier = fastest ? "Top" : "Bottom";
        System.out.println(qualifier + " " + limit + " " + descriptor + " speed values:");
        for (int i = 0; i < limit; i++) {
            int speed = speeds.get(i);
            TreeSet<PokemonCharacter> names = groups.get(speed);
            System.out.println("Speed " + speed + " (" + names.size() + " characters):");
            printCharacterDetails(names);
        }
    }

    private static void handleTopSpeedGroupsBySize() {
        Map<Integer, TreeSet<PokemonCharacter>> groups = buildSpeedBuckets();
        if (groups.isEmpty()) {
            System.out.println("No speed data available.");
            return;
        }

        ArrayList<Map.Entry<Integer, TreeSet<PokemonCharacter>>> entries = new ArrayList<>(groups.entrySet());
        entries.sort((a, b) -> {
            int sizeCompare = Integer.compare(b.getValue().size(), a.getValue().size());
            if (sizeCompare != 0) {
                return sizeCompare;
            }
            return Integer.compare(b.getKey(), a.getKey());
        });

        String[] keys = {"first", "second", "third"};
        TreeMap<String, TreeSet<String>> rankedGroups = new TreeMap<>();
        Map<String, Integer> keyToSpeed = new HashMap<>();

        for (int i = 0; i < Math.min(keys.length, entries.size()); i++) {
            TreeSet<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            for (PokemonCharacter character : entries.get(i).getValue()) {
                names.add(character.getName());
            }
            rankedGroups.put(keys[i], names);
            keyToSpeed.put(keys[i], entries.get(i).getKey());
        }

        if (rankedGroups.isEmpty()) {
            System.out.println("No speed data available.");
            return;
        }

        System.out.println("Top 3 speed groups by size:");
        for (String key : keys) {
            if (!rankedGroups.containsKey(key)) {
                continue;
            }
            TreeSet<String> names = rankedGroups.get(key);
            int speed = keyToSpeed.get(key);
            System.out.println(capitalize(key) + " place (speed " + speed + ", " + names.size() + " characters):");
            printCharacterDetailsForNames(names);
        }
    }

    private static void handleLargestSpeedGroup() {
        Map<Integer, TreeSet<PokemonCharacter>> groups = buildSpeedBuckets();
        if (groups.isEmpty()) {
            System.out.println("No speed data available.");
            return;
        }

        Map.Entry<Integer, TreeSet<PokemonCharacter>> largest = groups.entrySet().stream()
                .max((a, b) -> {
                    int sizeCompare = Integer.compare(a.getValue().size(), b.getValue().size());
                    if (sizeCompare != 0) {
                        return sizeCompare;
                    }
                    return Integer.compare(a.getKey(), b.getKey());
                })
                .orElse(null);

        if (largest == null) {
            System.out.println("No speed data available.");
            return;
        }

        System.out.println("Largest speed group (speed " + largest.getKey() + ", " + largest.getValue().size() + " characters):");
        printCharacterDetails(largest.getValue());
    }

    private static void buildSearchIndex() {
        NAME_TO_ROW.clear();
        if (rawData.isEmpty()) {
            return;
        }

        int nameIndex = PokemonCsvUtils.findColumnIndex(rawData.get(0), NAME_COLUMN);
        if (nameIndex < 0) {
            return;
        }

        for (int i = 1; i < rawData.size(); i++) {
            String row = rawData.get(i);
            List<String> tokens = PokemonCsvUtils.splitCsvRecord(row);
            if (nameIndex < tokens.size()) {
                String name = tokens.get(nameIndex).trim();
                if (!name.isEmpty()) {
                    NAME_TO_ROW.put(name.toLowerCase(), row);
                }
            }
        }
    }

    private static void buildPokemonCharacters() {
        resetCharacterCollections();
        if (rawData.size() <= 1) {
            return;
        }

        String header = rawData.get(0);
        int nameIndex = PokemonCsvUtils.findColumnIndex(header, NAME_COLUMN);
        int japaneseIndex = PokemonCsvUtils.findColumnIndex(header, JAPANESE_NAME_COLUMN);
        int hpIndex = PokemonCsvUtils.findColumnIndex(header, HP_COLUMN);
        int speedIndex = PokemonCsvUtils.findColumnIndex(header, SPEED_COLUMN);

        if (nameIndex < 0 || japaneseIndex < 0 || hpIndex < 0 || speedIndex < 0) {
            System.out.println("Unable to locate required columns for character data.");
            return;
        }

        for (int i = 1; i < rawData.size(); i++) {
            List<String> tokens = PokemonCsvUtils.splitCsvRecord(rawData.get(i));
            if (nameIndex >= tokens.size() || hpIndex >= tokens.size() || speedIndex >= tokens.size()) {
                continue;
            }

            String name = tokens.get(nameIndex).trim();
            String japaneseName = japaneseIndex < tokens.size() ? tokens.get(japaneseIndex).trim() : "";
            Integer hp = parseInteger(tokens.get(hpIndex));
            Integer speed = parseInteger(tokens.get(speedIndex));

            if (name.isEmpty() || hp == null || speed == null) {
                continue;
            }

            PokemonCharacter character = new PokemonCharacter(name, japaneseName, hp, speed);
            CHARACTERS.add(character);
            CHARACTER_BY_NAME.put(name.toLowerCase(), character);
            minHpValue = Math.min(minHpValue, hp);
            maxHpValue = Math.max(maxHpValue, hp);
            minSpeedValue = Math.min(minSpeedValue, speed);
            maxSpeedValue = Math.max(maxSpeedValue, speed);
        }
    }

    private static void resetCharacterCollections() {
        CHARACTERS.clear();
        CHARACTER_BY_NAME.clear();
        minHpValue = Integer.MAX_VALUE;
        maxHpValue = Integer.MIN_VALUE;
        minSpeedValue = Integer.MAX_VALUE;
        maxSpeedValue = Integer.MIN_VALUE;
    }

    private static boolean hasCharacterData() {
        if (CHARACTERS.isEmpty()) {
            System.out.println("Load data before searching.");
            return false;
        }
        return true;
    }

    private static void displayRange(String label, int min, int max) {
        if (min == Integer.MAX_VALUE || max == Integer.MIN_VALUE) {
            System.out.println("No " + label.toLowerCase() + " data available.");
        } else {
            System.out.println("Available " + label + " range: " + min + " - " + max);
        }
    }

    private static int promptForIntWithinRange(String prompt, int min, int max) {
        if (min == Integer.MAX_VALUE || max == Integer.MIN_VALUE) {
            return promptForInt(prompt);
        }
        while (true) {
            int value = promptForInt(prompt);
            if (value < min || value > max) {
                System.out.println("Please enter a value between " + min + " and " + max + ".");
                continue;
            }
            return value;
        }
    }

    private static TreeSet<PokemonCharacter> collectCharactersByHpRange(int minHp, int maxHp) {
        TreeSet<PokemonCharacter> matches = new TreeSet<>(PokemonCharacter.BY_HP_ASC);
        for (PokemonCharacter character : CHARACTERS) {
            if (character.getHp() >= minHp && character.getHp() <= maxHp) {
                matches.add(character);
            }
        }
        return matches;
    }

    private static TreeSet<PokemonCharacter> collectCharactersBySpeedRange(int minSpeed, int maxSpeed) {
        TreeSet<PokemonCharacter> matches = new TreeSet<>(PokemonCharacter.BY_SPEED_ASC);
        for (PokemonCharacter character : CHARACTERS) {
            if (character.getSpeed() >= minSpeed && character.getSpeed() <= maxSpeed) {
                matches.add(character);
            }
        }
        return matches;
    }

    private static Map<Integer, TreeSet<PokemonCharacter>> buildSpeedBuckets() {
        Map<Integer, TreeSet<PokemonCharacter>> groups = new HashMap<>();
        for (PokemonCharacter character : CHARACTERS) {
            groups.computeIfAbsent(character.getSpeed(), k -> new TreeSet<>(PokemonCharacter.BY_SPEED_ASC))
                    .add(character);
        }
        return groups;
    }

    private static void printCharacterDetails(TreeSet<PokemonCharacter> characters) {
        for (PokemonCharacter character : characters) {
            System.out.println(" - " + character.formatDetails());
        }
    }

    private static void printCharacterDetailsForNames(TreeSet<String> names) {
        for (String name : names) {
            PokemonCharacter character = findCharacterByName(name);
            if (character != null) {
                System.out.println(" - " + character.formatDetails());
            }
        }
    }

    private static PokemonCharacter findCharacterByName(String name) {
        if (name == null) {
            return null;
        }
        PokemonCharacter character = CHARACTER_BY_NAME.get(name.toLowerCase());
        if (character != null) {
            return character;
        }
        for (PokemonCharacter candidate : CHARACTERS) {
            if (candidate.getName().equalsIgnoreCase(name)) {
                return candidate;
            }
        }
        return null;
    }

    private static String capitalize(String word) {
        if (word == null || word.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    private static Integer parseInteger(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static int promptForInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            if (input.matches("-?\\d+")) {
                try {
                    return Integer.parseInt(input);
                } catch (NumberFormatException ex) {
                    System.out.println("Number is out of range. Try again.");
                }
            } else {
                System.out.println("Please enter a valid integer.");
            }
        }
    }
}
