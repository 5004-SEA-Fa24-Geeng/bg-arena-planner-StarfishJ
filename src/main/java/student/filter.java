package student;

import static student.Operations.getOperatorLenFromStr;

/**
 * The Filter class provides functionality for filtering board games based on various criteria.
 * It implements a flexible filtering system that allows users to filter games based on different
 * attributes such as name, player count, play time, difficulty, etc.
 *
 * The class supports various comparison operations including:
 * - Greater than (>)
 * - Less than (<)
 * - Equal to (==)
 * - Not equal to (!=)
 * - Contains (~=)
 * - Greater than or equal to (>=)
 * - Less than or equal to (<=)
 *
 * Example usage:
 * Filter.parseCondition("minPlayers>2") - finds games that support more than 2 players
 * Filter.parseCondition("name~=chess") - finds games with "chess" in their name
 * Filter.parseCondition("rating>=8.0") - finds games rated 8.0 or higher
 *
 * @author Yuchen Huang
 * @version 1.0
 */
public final class Filter {
    
    private final GameData column;
    private final Operations operation;
    private final String value;

    private Filter(GameData column, Operations operation, String value) {
        this.column = column;
        this.operation = operation;
        this.value = value;
    }

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

    public boolean apply(BoardGame game) {
        Comparable<?> gameValue = getGameValue(game);
        Comparable<?> filterValue = parseFilterValue();

        if (gameValue == null || filterValue == null) {
            return false;
        }

        if (gameValue.getClass().isInstance(filterValue)) {
            @SuppressWarnings("unchecked")
            Comparable<Object> typedGameValue = (Comparable<Object>) gameValue;
            return switch (operation) {
                case GREATER_THAN -> typedGameValue.compareTo(filterValue) > 0;
                case LESS_THAN -> typedGameValue.compareTo(filterValue) < 0;
                case GREATER_THAN_EQUALS -> typedGameValue.compareTo(filterValue) >= 0;
                case LESS_THAN_EQUALS -> typedGameValue.compareTo(filterValue) <= 0;
                case EQUALS -> typedGameValue.compareTo(filterValue) == 0;
                case NOT_EQUALS -> typedGameValue.compareTo(filterValue) != 0;
                case CONTAINS -> gameValue.toString().toLowerCase()
                        .contains(filterValue.toString().toLowerCase());
                default -> false;
            };
        }
        return false;
    }

    private Comparable<?> getGameValue(BoardGame game) {
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

    private Comparable<?> parseFilterValue() {
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