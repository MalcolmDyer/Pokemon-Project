import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
    //TODO create constants for sub menu 3 options instead of them being hard coded

    // ==== sub menu 4 - attribute: speed
    //TODO create constants for sub menu 3 options instead of them being hard coded

    private static final String DEFAULT_FILE = "pokemon.csv";
    private static final String RESULTS_FILE = "character_names.txt";
    private static final int MAX_FILENAME_ATTEMPTS = 2;
    private static final String NAME_COLUMN = "name";

    private static final IReadData DATA_READER = new ReadData();
    private static final IAnalyzePokemonData DATA_ANALYZER = new AnalyzePokemonData();
    private static final TestData TEST_DATA = new TestData(new WriteData());

    private static ArrayList<String> rawData = new ArrayList<>();
    private static HashSet<String> cachedNames = new HashSet<>();
    private static ArrayList<String> sortedNames = new ArrayList<>();
    private static final Map<String, String> NAME_TO_ROW = new HashMap<>();

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
            System.out.println("Successfully loaded " + rawData.size() + " rows.");
            return;
        }

        rawData.clear();
        cachedNames.clear();
        sortedNames.clear();
        NAME_TO_ROW.clear();
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
        System.out.println("Search by Hit Points");
        System.out.println("0 - Go back to previous menu");
        System.out.println("1 - Find a character with a specific hit point value");
        System.out.println("2 - Find characters within a specific range of hit values");
        System.out.println("3 - Find the character with the lowest hit point value");
        System.out.println("4 - Find the character with the highest hit point value");
        System.out.print("Enter an option: ");

        String choice = SCANNER.nextLine().trim();
        if (choice.matches("[0-4]")) {
            return Integer.parseInt(choice);
        }

        System.out.println("Please enter a number between 0 and 4.");
    }
}

//      ==== Attribute menu: Speed ====
private static int showSubMenu4() {
    while (true) {
        System.out.println("Search by Speed");
        System.out.println("0 - Go back to previous menu");
        System.out.println("1 - Which character has the fastest speed");
        System.out.println("2 - Which character has the slowest speed");
        System.out.println("3 - Which characters are part of the top 3 fastest speeds");
        System.out.println("4 - Which characters are part of the 3 slowest speeds");
        System.out.println("5 - Which characters are part of a specific range of speeds");
        System.out.println("6 - What are the top 3 speed groups, and what is the list of characters which are part of each speed group");
        System.out.println("7 - Which group of characters represent the largest speed group");
        System.out.print("Enter an option: ");

        String choice = SCANNER.nextLine().trim();
        if (choice.matches("[0-7]")) {
            return Integer.parseInt(choice);
        }

        System.out.println("Please enter a number between 0 and 7.");
    }
}


    public static void handleSearchByAttributes() {
        if (sortedNames.isEmpty() || NAME_TO_ROW.isEmpty()) {
            System.out.println("Load data before searching.");
            return;
        }

        int subOption = showSubMenu2();
        switch (subOption) {
            case SUB_MENU_BACK:
                return;
            case SUB_MENU2_SEARCH_BY_HITPOINTS:
                showSubMenu3();
                break;
            case SUB_MENU2_SEARCH_BY_SPEED:
                showSubMenu4();
                break;
            default:
                System.out.println("Unknown sub-option selected.");
        }
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
}
