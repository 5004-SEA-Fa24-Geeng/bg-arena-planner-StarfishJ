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
 * @author Yuchen Huang
 * @version 1.0
 */
public class SortCriterion {
    private final GameData field;
    private final boolean ascending;

    /**
     * Constructor for SortCriterion.
     * @param field The field to sort by
     * @param ascending Whether to sort in ascending order
     */
    public SortCriterion(GameData field, boolean ascending) {
        this.field = field;
        this.ascending = ascending;
    }

    /**
     * Creates a comparator for the specified sort criterion.
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