package student;

/**
 * GameData is an enumeration that defines the various attributes of board games that can be
 * used for filtering and sorting operations in the Board Game Arena Planner.
 * 
 * Each enum constant represents a specific attribute of a board game and maps to a corresponding
 * column name in the CSV data file. This mapping allows for flexible data loading and manipulation
 * while maintaining a clean separation between the internal representation and external data format.
 *
 * The enum provides methods for:
 * - Converting between column names and enum values
 * - Converting between string representations and enum values
 * - Accessing the original column names from the CSV file
 *
 * Supported attributes include:
 * - NAME: The name of the board game
 * - ID: Unique identifier for the game
 * - RATING: Average user rating
 * - DIFFICULTY: Game complexity/weight
 * - RANK: Overall ranking
 * - MIN/MAX_PLAYERS: Player count range
 * - MIN/MAX_TIME: Play time range
 * - YEAR: Publication year
 *
 * @author Yuchen Huang
 * @version 1.0
 */
public enum GameData {
    /**
     * Enums matching CODE(cvsname) pattern.
     * name and id are used for game uniqueness.
     */
    NAME("objectname"), ID("objectid"),
    /** Enums that are based on double values in the csv file. */
    RATING("average"), DIFFICULTY("avgweight"),
    /** Enums based on whole int values in the csv file. */
    RANK("rank"), MIN_PLAYERS("minplayers"), MAX_PLAYERS("maxplayers"),
    /** More int based columns. */
    MIN_TIME("minplaytime"), MAX_TIME("maxplaytime"), YEAR("yearpublished");

    /** stores the original csv name in the enum. */
    private final String columnName;

    /**
     * Constructor for the enum.
     * @param columnName the name of the column in the CSV file.
     */
    GameData(String columnName) {
        this.columnName = columnName;
    }

    /**
     * Getter for the column name.
     * @return the name of the column in the CSV file.
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Get the enum from the column name.
     * @param columnName the name of the column in the CSV file.
     * @return the enum that matches the column name.
     */
    public static GameData fromColumnName(String columnName) {
        for (GameData col : GameData.values()) {
            if (col.getColumnName().equals(columnName)) {
                return col;
            }
        }
        throw new IllegalArgumentException("No column with name " + columnName);
    }

    /**
     * Get the enum from the enum name.
     * Can use the enum name or the column name. Useful for filters and sorts
     * as they can use both.
     * @param name the name of the enum.
     * @return the enum that matches the name.
     */
    public static GameData fromString(String name) {
        for (GameData col : GameData.values()) {
            if (col.name().equalsIgnoreCase(name) || col.getColumnName().equalsIgnoreCase(name)) {
                return col;
            }
        }
        throw new IllegalArgumentException("No column with name " + name);
    }

}
