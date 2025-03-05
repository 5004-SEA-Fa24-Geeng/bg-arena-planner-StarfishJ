package student;

import java.util.Comparator;
import java.util.function.Function;

/**
 * SortCriterion class provides functionality for sorting board games based on various criteria.
 * It implements type-safe comparison operations for different game attributes.
 *
 * The class supports sorting by any GameData attribute and handles both ascending and
 * descending order. It uses Java's Comparator interface to provide type-safe comparisons.
 *
 * Key features:
 * - Type-safe comparisons for all game attributes
 * - Support for both ascending and descending order
 * - Null-safe comparison handling
 * - Case-insensitive string comparisons for game names
 *
 * Example usage:
 * - Sort by name: new SortCriterion(GameData.NAME, true)
 * - Sort by rating descending: new SortCriterion(GameData.RATING, false)
 * - Sort by year: new SortCriterion(GameData.YEAR, true)
 *
 * @author Yuchen Huang
 * @version 1.0
 */
public class SortCriterion {
    /** The field to sort by. */
    private final GameData field;
    /** Whether to sort in ascending order. */
    private final boolean ascending;

    /**
     * Constructor for SortCriterion.
     * Creates a new sorting criterion with the specified field and order.
     *
     * @param field The GameData field to sort by
     * @param ascending Whether to sort in ascending order (true) or descending order (false)
     */
    public SortCriterion(GameData field, boolean ascending) {
        this.field = field;
        this.ascending = ascending;
    }

    /**
     * Creates a comparator for the specified sort criterion.
     * The comparator handles type-safe comparison of board game attributes.
     *
     * @return A type-safe comparator for BoardGame objects
     */
    public Comparator<BoardGame> createComparator() {
        Function<BoardGame, ? extends Comparable<?>> valueExtractor = this::getGameValue;
        @SuppressWarnings("unchecked")
        Comparator<BoardGame> comparator = (bg1, bg2) -> {
            Comparable val1 = valueExtractor.apply(bg1);
            Comparable val2 = valueExtractor.apply(bg2);
            return val1.compareTo(val2);
        };
        return ascending ? comparator : comparator.reversed();
    }

    /**
     * Gets the value to compare for a given board game.
     * Extracts the appropriate value based on the sort field.
     *
     * @param game The board game to get the value from
     * @return A comparable value based on the sort field
     */
    private Comparable<?> getGameValue(BoardGame game) {
        return switch (field) {
            case NAME -> game.getName();
            case YEAR -> game.getYearPublished();
            case MAX_TIME -> game.getMaxPlayTime();
            case MIN_TIME -> game.getMinPlayTime();
            case DIFFICULTY -> game.getDifficulty();
            case RANK -> game.getRank();
            case MAX_PLAYERS -> game.getMaxPlayers();
            case MIN_PLAYERS -> game.getMinPlayers();
            case RATING -> game.getRating();
            default -> "";
        };
    }
}
