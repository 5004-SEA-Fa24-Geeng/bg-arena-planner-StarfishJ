package student;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.Random;
import java.io.InputStream;


/**
 * Primary application that makes use of the IGameList and IPlanner interfaces.
 * The console app is an interactive way to manage the BG Arena game list,
 * and for a client to build a list of games they want to play, and
 * save out that list. Most of the features are focused on
 * providing a progressive filter to find games, and then add them to the list.
 */
public class ConsoleApp {
    /** Interaction with the system terminal/command line. */
    private static Scanner input;
    /** Default name to save the game list to. */
    private static final String DEFAULT_FILENAME = "games_list.txt";
    /** random number generator only needs to be built once. */
    private static final Random RND = new Random();
    /** scanner to help with processing the command string. */
    private Scanner current;
    /** The game list to manage. */
    private final IGameList gameList;
    /** The planner to help filter games. */
    private final IPlanner planner;

    /**
     * Constructor for the console app.
     * 
     * @param gameList the game list to manage.
     * @param planner  the planner to help filter games.
     */
    public ConsoleApp(IGameList gameList, IPlanner planner) {
        this.gameList = gameList;
        this.planner = planner;
        if (input == null) {
            input = new Scanner(System.in);
        }
    }    

    /**
     * Start the console application.
     * Processes the main menu commands and redirects.
     */
    public void start() {
        printOutput("%s%n", ConsoleText.WELCOME);
        ConsoleText ct = nextCommand();
        while (ct != ConsoleText.CMD_EXIT) {
            switch (ct) {
                case CMD_QUESTION: // same as help
                case CMD_HELP:
                    processHelp();
                    break;
                case CMD_FILTER:
                    processFilter();
                    break;
                case CMD_LIST:
                    processListCommands();
                    break;
                case CMD_EASTER_EGG:
                    randomNumber();
                    break;
                case INVALID:
                default:
                    printOutput("%s%n", ConsoleText.INVALID);
            }

            // clean up scanner.
            if (current != null) {
                current.close();
                current = null;
            }
            // get the next prompt
            ct = nextCommand();
        }

        printOutput("%s%n", ConsoleText.GOODBYE);
        if (input != null) {
            input.close();
            input = null;
        }
    }

    /**
     * Generate a random number based on the current filter.
     */
    private void randomNumber() {
        int max = (int) planner.filter("").count();
        if (max > 0) {
            int random = RND.nextInt(max) + 1; // random is 0-(max-1) so add 1.
            printOutput("%s %d%n", ConsoleText.EASTER_EGG, random);
        }
        // else do nothing, not a secret easter egg if filter is empty.
    }

    /**
     * Process the help command.
     */
    private void processHelp() {
        ConsoleText ct = ConsoleText.CMD_HELP;
        if (current.hasNext()) {
            ct = nextCommand();
        }
        switch (ct) {
            case CMD_FILTER:
                printOutput("%s%n", ConsoleText.FILTER_HELP);
                break;
            case CMD_LIST:
                printOutput("%s%n", ConsoleText.LIST_HELP);
                break;
            default:
                printOutput("%s%n", ConsoleText.HELP);
        }
    }

    /**
     * Process the filter command.
     */
    private void processFilter() {
        Stream<BoardGame> result = null;
        GameData sortON = GameData.NAME; // default

        if (current.hasNext()) {
            String filter = remainder();
            // keep space for all name operations
            if (filter.toLowerCase().trim().startsWith("name")) {
                String[] parts = null;
                // check different operators
                if (filter.contains("~=")) {
                    parts = filter.split("~=", 2);
                    if (parts.length == 2) {
                        filter = parts[0].trim() + "~=" + parts[1].trim();
                    }
                } else if (filter.contains("==")) {
                    parts = filter.split("==", 2);
                    if (parts.length == 2) {
                        filter = parts[0].trim() + "==" + parts[1].trim();
                    }
                } else if (filter.contains("!=")) {
                    parts = filter.split("!=", 2);
                    if (parts.length == 2) {
                        filter = parts[0].trim() + "!=" + parts[1].trim();
                    }
                }
            } else {
                // remove all spaces for other operations
                filter = filter.replaceAll("\\s", "");
            }
            filter = filter.toLowerCase(); // make it lower case
            if (filter.equalsIgnoreCase(ConsoleText.CMD_QUESTION.toString())) {
                printOutput("%s%n", ConsoleText.FILTER_HELP);
                return; // leave early. only doing ? as help could be a game name.
            }

            if (filter.equalsIgnoreCase(ConsoleText.CMD_CLEAR.toString())) {
                planner.reset();
                printOutput("%s%n", ConsoleText.FILTERED_CLEAR);
                return; // leave early.
            }
            if (filter.contains(ConsoleText.CMD_SORT_OPTION.toString())) {
                // break it up, figure out sort
                boolean ascending = true; // default
                String[] parts = filter.split(ConsoleText.CMD_SORT_OPTION.toString());
                if (parts.length == 2) {
                    String sort = parts[1];
                    if (sort.contains(ConsoleText.CMD_SORT_OPTION_DIRECTION_ASC.toString())) {
                        ascending = true;
                        sort = sort.substring(0,
                                sort.indexOf(ConsoleText.CMD_SORT_OPTION_DIRECTION_ASC.toString()));
                    } else if (sort
                            .contains(ConsoleText.CMD_SORT_OPTION_DIRECTION_DESC.toString())) {
                        ascending = false;
                        sort = sort.substring(0, sort
                                .indexOf(ConsoleText.CMD_SORT_OPTION_DIRECTION_DESC.toString()));
                    }
                    try {
                        sortON = GameData.fromString(sort);
                    } catch (IllegalArgumentException e) {
                        printOutput("%s%n", ConsoleText.INVALID);
                        return; // leave early.
                    }
                }

                result = planner.filter(parts[0], sortON, ascending);  // NOTICE: sortON and ascending are used here.
            } else {
                result = planner.filter(filter); // default sort
            }
        } else {
            printOutput("%s%n", ConsoleText.NO_FILTER);
            result = planner.filter("");
        }
        printFilterStream(result, sortON);
    }

    /**
     * Print the filtered stream of games.
     * 
     * @param games  the stream of games to print.
     * @param sortON also is the column used for 'extra info' based on the sort
     *               type.
     */
    private static void printFilterStream(Stream<BoardGame> games, GameData sortON) {
        int counter = 1;
        List<BoardGame> gameList = games != null ? games.toList() : Collections.emptyList();
        for (BoardGame game : gameList) {
            printOutput("%d: %s%n", counter++, game.toStringWithInfo(sortON));
        }
    }

    /**
     * Process the list commands.
     */
    private void processListCommands() {
        ConsoleText ct = ConsoleText.INVALID;
        if (current.hasNext()) {
            ct = nextCommand();

            switch (ct) {
                case CMD_SHOW:
                    printCurrentList();
                    break;
                case CMD_CLEAR:
                    gameList.clear();
                    break;
                case CMD_ADD:
                    String toAdd = remainder().toLowerCase();
                    if (toAdd.isEmpty()) {
                        break;
                    }
                    try {
                        gameList.addToList(toAdd, planner.filter(""));
                    } catch (IllegalArgumentException e) {
                        printOutput("%s %s%n", ConsoleText.INVALID_LIST, toAdd);
                    }
                    break;
                case CMD_REMOVE:
                    String remove = remainder().toLowerCase();
                    if (remove.isEmpty()) {
                        break;
                    }
                    try {
                        gameList.removeFromList(remove);
                    } catch (IllegalArgumentException e) {
                        printOutput("%s %s%n", ConsoleText.INVALID_LIST, remove);
                    }
                    break;
                case CMD_SAVE:
                    String filename = remainder().trim();
                    if (filename.isEmpty()) {
                        filename = DEFAULT_FILENAME;
                    }
                    gameList.saveGame(filename);
                    break;
                case CMD_QUESTION:
                case CMD_HELP:
                    printOutput("%s%n", ConsoleText.LIST_HELP);
                    break;
                default:
                    printOutput("%s%n", ConsoleText.INVALID);
                    printOutput("%s%n", ConsoleText.LIST_HELP);
            }
        } else {
            printCurrentList(); // just print the list if "list" only is entered.
        }
    }

    /**
     * Print the current list of games.
     */
    private void printCurrentList() {
        if (gameList.count() > 0) {
            int counter = 1;
            for (String game : gameList.getGameNames()) {
                printOutput("%d: %s%n", counter++, game);
            }
        } else {
            printOutput("%s%n", ConsoleText.NO_GAMES_LIST);
        }

    }

    /**
     * Get the next command from the user.
     * @return the next command.
     */
    private ConsoleText nextCommand() {
        if (current == null || !current.hasNext()) {
            String line = getInput("%s", ConsoleText.PROMPT);
            if (line.isEmpty()) {
                return ConsoleText.CMD_EXIT; // 如果输入为空，优雅地退出
            }
            current = new Scanner(line.trim());
        }

        if (!current.hasNext()) {
            return ConsoleText.INVALID;
        }

        try {
            return ConsoleText.fromString(current.next());
        } catch (Exception e) {
            return ConsoleText.INVALID;
        }
    }

    /**
     * Get the remainder of the current line.
     * 
     * @return the remainder of the current line.
     */
    private String remainder() {
        return current != null && current.hasNext() ? current.nextLine().trim() : "";
    }

    /** 
     * Gets input from the client.
     * @param format the format string to print.
     * @param args   the arguments to the format string.
     * @return the input from the client as a string, one line at a time.
     */
    private static String getInput(String format, Object... args) {
        System.out.printf(format, args);
        System.out.flush(); // 确保提示信息被输出
        
        if (input == null) {
            input = new Scanner(System.in); // 如果 Scanner 为空，重新初始化
        }
        
        try {
            if (input.hasNextLine()) {
                return input.nextLine();
            }
        } catch (Exception e) {
            System.err.println("Error reading input: " + e.getMessage());
        }
        
        return "";
    }

    /** 
     * Prints output to the client.
     * We could call printf directly, but this gives us one location in case
     * we want to change the output to a file or other location.
     * @param format the format string to print.
     * @param output the output to print (array to match the format).
     */
    private static void printOutput(String format, Object... output) {
        System.out.printf(format, output);
    }

    /**
     * Enum to help with console text.
     * This enum uses a properties file (stored in resources), so that
     * we can easily change the text without changing the code. While not
     * fully setup, this is also a way to add localization to the application by
     * having different properties files for different languages.
     * It is worth noting this is *one* way to do this. It is also
     * very common to have a class with a number of final static strings
     * that are used for the same purpose - the most important part
     * is to separate the text from the code.
     */
    private enum ConsoleText {
        /** various commands and text. */
        WELCOME, HELP, INVALID, GOODBYE, PROMPT, NO_FILTER, NO_GAMES_LIST, FILTERED_CLEAR, LIST_HELP, FILTER_HELP,
        /** commands continued. */
        INVALID_LIST, EASTER_EGG, CMD_EASTER_EGG,
        /** commands general. */
        CMD_EXIT, CMD_HELP, CMD_QUESTION, CMD_FILTER, CMD_LIST,
        /** commands specific to lists and filters. */
        CMD_SHOW, CMD_ADD, CMD_REMOVE, CMD_CLEAR, CMD_SAVE,
        /** more options on commands. */
        CMD_OPTION_ALL, CMD_SORT_OPTION, CMD_SORT_OPTION_DIRECTION_ASC, CMD_SORT_OPTION_DIRECTION_DESC;

        /** load the files on class load. */
        private static final Properties CTEXT = new Properties();

        /**
         * Get the string representation of the enum.
         * This ends up being the value in the properties file with the key
         * being paired to the enum name, lowercase.
         * @return the string representation of the enum.
         */
        @Override
        public String toString() {
            return CTEXT.getProperty(this.name().toLowerCase());
        }

        /**
         * Get the enum from a string.
         * Has a null check in case the string is not found in the properties file.
         * @param str the string to convert to an enum.
         * @return the enum value.
         */
        public static ConsoleText fromString(String str) {
            for (ConsoleText ct : ConsoleText.values()) {
                if (ct.toString() != null && ct.toString().equalsIgnoreCase(str)) {
                    return ct;
                }
            }
            return ConsoleText.INVALID;
        }

        /*
         * The static location is used when the classes are loaded into
         * static memory which is done for the enum values.
         * 
         * This section then loads the properties file into the cText,
         * so that the enums can access the strings from the properties file.
         * 
         * The /console.properties file is stored in the resources directory,
         * using the base resources location as the root.
         */
        static {
            try {
                InputStream is = ConsoleApp.class.getResourceAsStream("/console.properties");
                if (is != null) {
                    CTEXT.loadFromXML(is);
                } else {
                    System.err.println("Warning: Could not find console.properties");
                    // 设置一些默认值
                    CTEXT.setProperty("welcome", "*******Welcome to the BoardGame Arena Planner! *******");
                    CTEXT.setProperty("prompt", "> ");
                    CTEXT.setProperty("exit", "exit");
                    CTEXT.setProperty("help", "help");
                    CTEXT.setProperty("invalid", "Invalid command. Type 'help' for available commands.");
                }
            } catch (Exception e) {
                System.err.println("Error loading properties: " + e.getMessage());
            }
        }
    }
}
