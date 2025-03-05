package student;

/**
 * The Filter class provides functionality for filtering board games based on various criteria.
 * It implements a flexible filtering system that allows users to filter games based on different
 * attributes such as name, player count, play time, difficulty, etc.
 * <p>
 * The class supports various comparison operations including:
 * - Greater than (>)
 * - Less than (<)
 * - Equal to (==)
 * - Not equal to (!=)
 * - Contains (~=)
 * - Greater than or equal to (>=)
 * - Less than or equal to (<=)
 * <p>
 * Example usage:
 * - Filter.parseCondition("minPlayers>2") - finds games that support more than 2 players
 * - Filter.parseCondition("name~=chess") - finds games with "chess" in their name
 * - Filter.parseCondition("rating>=8.0") - finds games rated 8.0 or higher
 *
 * @author Yuchen Huang
 * @version 1.0
 */
public final class Filter {

    /**
     * The game data column to filter on.
     */
    private final GameData column;
    /**
     * The operation to apply for filtering.
     */
    private final Operations operation;
    /**
     * The value to compare against.
     */
    private final String value;

    /**
     * Private constructor for Filter class.
     * Use {@link #parseCondition(String)} to create a new Filter instance.
     *
     * @param column    The game data column to filter on
     * @param operation The operation to apply
     * @param value     The value to compare against
     */
    private Filter(GameData column, Operations operation, String value) {
        this.column = column;
        this.operation = operation;
        this.value = value;
    }

    /**
     * Parses a condition string and creates a new Filter instance.
     * The condition string should be in the format: "column operator value"
     *
     * @param condition The condition string to parse
     * @return A new Filter instance, or null if the condition is invalid
     */
    public static Filter parseCondition(String condition) {
        if (condition == null || condition.trim().isEmpty()) {
            return null;
        }

        // Find the operator first
        Operations op = null;
        int opIndex = -1;

        for (Operations operation : Operations.values()) {
            String operator = operation.getOperator();
            int index = condition.indexOf(operator);
            if (index > 0) {
                if (opIndex == -1 
                    || index < opIndex 
                    || (index == opIndex && operator.length() > (op != null ? op.getOperator().length() : 0))) {
                    op = operation;
                    opIndex = index;
                }
            }
        }

        if (op == null || opIndex <= 0) {
            return null;
        }

        String columnName = condition.substring(0, opIndex).trim();
        String value = condition.substring(opIndex + op.getOperator().length());

        // Preserve spaces for all name-related operations
        if (columnName.equalsIgnoreCase("name")) {
            value = value.trim();
            // Remove any leading operator characters that might have been included
            if (value.startsWith("=")) {
                value = value.substring(1).trim();
            }
            // Handle the case where the value might start with other operator characters
            if (value.startsWith(">") || value.startsWith("<") || 
                value.startsWith("~") || value.startsWith("!")) {
                value = value.substring(1).trim();
            }
        } else {
            // Remove all spaces for other operations
            value = value.replaceAll("\\s+", "");
            // Handle numeric operations the same way
            if (value.startsWith("=")) {
                value = value.substring(1).trim();
            }
        }

        GameData column = GameData.fromString(columnName);
        if (column == null) {
            return null;
        }

        return new Filter(column, op, value);
    }

    /**
     * Applies the filter to a board game.
     *
     * @param game The board game to check
     * @return true if the game matches the filter condition, false otherwise
     */
    public boolean apply(BoardGame game) {
        if (game == null) {
            return false;
        }

        // Special handling for name operations
        if (column == GameData.NAME) {
            String gameName = game.getName();
            if (gameName == null) {
                return false;
            }
            
            // All name comparisons should be case-insensitive
            String searchTerm = value.toLowerCase();
            String gameNameLower = gameName.toLowerCase();
            
            // Special handling for CONTAINS operation
            if (operation == Operations.CONTAINS) {
                return gameNameLower.contains(searchTerm);
            }
            
            // For other name operations, use complete string comparison
            int comparison = gameNameLower.compareTo(searchTerm);
            
            switch (operation) {
                case EQUALS:
                    return comparison == 0;
                case NOT_EQUALS:
                    return comparison != 0;
                case GREATER_THAN:
                    return comparison > 0;
                case LESS_THAN:
                    return comparison < 0;
                case GREATER_THAN_EQUALS:
                    return comparison >= 0;
                case LESS_THAN_EQUALS:
                    return comparison <= 0;
                default:
                    return false;
            }
        }

        // Handle other operations
        Comparable<?> gameValue = getGameValue(game);
        Comparable<?> filterValue = parseFilterValue();

        if (gameValue == null || filterValue == null) {
            return false;
        }

        // for other operations, we need to ensure type matching
        if (gameValue.getClass().isInstance(filterValue)) {
            @SuppressWarnings("unchecked")
            Comparable<Object> typedGameValue = (Comparable<Object>) gameValue;
            int comparison = typedGameValue.compareTo(filterValue);
            
            if (operation == Operations.EQUALS) {
                return comparison == 0;
            }
            if (operation == Operations.NOT_EQUALS) {
                return comparison != 0;
            }
            if (operation == Operations.GREATER_THAN) {
                return comparison > 0;
            }
            if (operation == Operations.LESS_THAN) {
                return comparison < 0;
            }
            if (operation == Operations.GREATER_THAN_EQUALS) {
                return comparison >= 0;
            }
            if (operation == Operations.LESS_THAN_EQUALS) {
                return comparison <= 0;
            }
        }
        return false;
    }

    /**
     * Gets the game value for comparison based on the column.
     *
     * @param game The board game to get the value from
     * @return The comparable value from the game
     */
    private Comparable<?> getGameValue(BoardGame game) {
        if (column == GameData.NAME) {
            return game.getName();
        }
        if (column == GameData.YEAR) {
            return game.getYearPublished();
        }
        if (column == GameData.MAX_TIME) {
            return game.getMaxPlayTime();
        }
        if (column == GameData.MIN_TIME) {
            return game.getMinPlayTime();
        }
        if (column == GameData.DIFFICULTY) {
            return game.getDifficulty();
        }
        if (column == GameData.RANK) {
            return game.getRank();
        }
        if (column == GameData.MAX_PLAYERS) {
            return game.getMaxPlayers();
        }
        if (column == GameData.MIN_PLAYERS) {
            return game.getMinPlayers();
        }
        if (column == GameData.RATING) {
            return game.getRating();
        }
        return null;
    }

    /**
     * Parses the filter value to a comparable type.
     *
     * @return The parsed filter value as a Comparable object
     */
    private Comparable<?> parseFilterValue() {
        try {
            if (column == GameData.NAME) {
                return value;
            }
            if (column == GameData.YEAR 
                || column == GameData.RANK 
                || column == GameData.MAX_PLAYERS 
                || column == GameData.MIN_PLAYERS) {
                return Integer.parseInt(value);
            }
            if (column == GameData.MAX_TIME 
                || column == GameData.MIN_TIME) {
                return Integer.parseInt(value);
            }
            if (column == GameData.DIFFICULTY 
                || column == GameData.RATING) {
                return Double.parseDouble(value);
            }
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Gets the operation used in this filter.
     *
     * @return The operation
     */
    public Operations getOperation() {
        return operation;
    }

    /**
     * Gets the column used in this filter.
     *
     * @return The column
     */
    public GameData getColumn() {
        return column;
    }

    /**
     * Gets the value used in this filter.
     *
     * @return The value as a string
     */
    public String getValue() {
        return value;
    }
}
