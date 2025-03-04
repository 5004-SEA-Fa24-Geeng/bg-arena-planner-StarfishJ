package student;

import java.util.Comparator;

/**
 * Class to represent a sorting criterion for board games.
 */
public class SortCriterion {
    /* column to sort on */
    private final GameData sortOn;
    /* true for ascending order, false for descending */
    private final boolean ascending;

    /**
     * Constructor for SortCriterion.
     * @param sortOn    the column to sort on
     * @param ascending true for ascending order, false for descending
     */
    public SortCriterion(GameData sortOn, boolean ascending) {
        if (sortOn == GameData.ID) {
            throw new IllegalArgumentException("Sorting by ID is not allowed.");
        }
        this.sortOn = sortOn;
        this.ascending = ascending;
    }

    /**
     * Returns a Comparator based on the sorting criteria.
     */
    public Comparator<BoardGame> getComparator() {
        Comparator<BoardGame> comparator = Comparator.comparing(this::getGameValue);

        if (!ascending) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

    /**
     * Extracts the comparable value from the game based on the sorting column.
     */
    private Comparable getGameValue(BoardGame game) {
        return switch (sortOn) {
            case NAME -> game.getName();
            case YEAR -> game.getYearPublished();
            case MAX_TIME -> game.getMaxPlayTime();
            case MIN_TIME -> game.getMinPlayTime();
            case DIFFICULTY -> game.getDifficulty();
            case RANK -> game.getRank();
            case MAX_PLAYERS -> game.getMaxPlayers();
            case MIN_PLAYERS -> game.getMinPlayers();
            case RATING -> game.getRating();
            default -> throw new IllegalStateException("Unexpected sorting column: " + sortOn);
        };
    }
}