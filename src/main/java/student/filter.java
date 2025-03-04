package student;

import static student.Operations.getOperatorLenFromStr;

/**
 * Utility class to handle filter parsing and application for board games.
 */
public final class Filter {
    /* column to filter on */
    private final GameData column;
    /* operation to apply */
    private final Operations operation;
    /* value to compare against */
    private final String value;

    /**
     * Constructor for Filter.
     *
     * @param column    the column to filter on
     * @param operation the operation to apply
     * @param value     the value to compare against
     */
    private Filter(GameData column, Operations operation, String value) {
        this.column = column;
        this.operation = operation;
        this.value = value;
    }

    /**
     * Parses a condition string into a Filter object.
     *
     * @param condition the condition string to parse
     * @return a Filter object or null if parsing fails
     */
    public static Filter parseCondition(String condition) {
        if (condition == null || condition.trim().isEmpty()) {
            return null;
        }

        condition = condition.replaceAll("\\s+", "");
        //System.out.println("Parsing condition: " + condition);

        Operations op = null;
        int opIndex = -1;

        for (Operations operation : Operations.values()) {
            String operator = operation.getOperator();
            int index = condition.indexOf(operator);
            if (index > 0) {
                if (opIndex == -1 || index < opIndex) {
                    op = operation;
                    opIndex = index;
                }
            }
        }

        if (op == null || opIndex == -1) {
            return null;
        }

        String columnName = condition.substring(0, opIndex).trim();
        String value = condition.substring(opIndex + getOperatorLenFromStr(condition)).trim();

        //System.out.println(columnName);
        //System.out.println(getOperatorFromStr(condition));
        //System.out.println(value);

        GameData column = GameData.fromString(columnName);
        if (column == null) {
            return null;
        }

        return new Filter(column, op, value);
    }

    /**
     * Applies the filter to a BoardGame object.
     *
     * @param game the BoardGame object to apply the filter to
     * @return true if the game matches the filter, false otherwise
     */
    public boolean apply(BoardGame game) {
        Comparable gameValue = getGameValue(game);
        Comparable filterValue = parseFilterValue();

        if (gameValue == null || filterValue == null) {
            return false;
        }

        return switch (operation) {
            case GREATER_THAN -> gameValue.compareTo(filterValue) > 0;
            case LESS_THAN -> gameValue.compareTo(filterValue) < 0;
            case GREATER_THAN_EQUALS -> gameValue.compareTo(filterValue) >= 0;
            case LESS_THAN_EQUALS -> gameValue.compareTo(filterValue) <= 0;
            case EQUALS -> gameValue.compareTo(filterValue) == 0;
            case NOT_EQUALS -> gameValue.compareTo(filterValue) != 0;
            case CONTAINS -> gameValue.toString().toLowerCase()
                    .contains(filterValue.toString().toLowerCase());
            default -> false;
        };
    }

    /**
     * Returns the column to filter on.
     * @return the column to filter on
     */
    private Comparable getGameValue(BoardGame game) {
        return switch (column) {
            case NAME -> game.getName();
            case YEAR -> game.getYearPublished();
            case MAX_TIME -> game.getMaxPlayTime();
            case MIN_TIME -> game.getMinPlayTime();
            case DIFFICULTY -> game.getDifficulty();
            case RANK -> game.getRank();
            case MAX_PLAYERS -> game.getMaxPlayers();
            case MIN_PLAYERS -> game.getMinPlayers();
            case RATING -> game.getRating();
            default -> null;
        };
    }

    /**
     * Parses the filter value based on the column type.
     * @return the parsed filter value
     */
    private Comparable parseFilterValue() {
        try {
            return switch (column) {
                case NAME -> value;
                case YEAR, MAX_TIME, MIN_TIME, MAX_PLAYERS, MIN_PLAYERS, RANK -> Integer.parseInt(value);
                case DIFFICULTY, RATING -> Double.parseDouble(value);
                default -> null;
            };
        } catch (NumberFormatException e) {
            return null;
        }
    }
}