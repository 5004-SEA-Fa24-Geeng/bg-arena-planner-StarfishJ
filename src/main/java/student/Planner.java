package student;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Planner implements the IPlanner interface and serves as the core filtering and sorting
 * engine for the Board Game Arena Planner. This class manages a collection of board games
 * and provides sophisticated filtering and sorting capabilities.
 *
 * Key features:
 * - Progressive filtering: Each filter operation builds upon previous filters.
 * - Multiple filter criteria: Support for combining multiple conditions.
 * - Flexible sorting: Sort results by any game attribute.
 * - Bidirectional sorting: Support for both ascending and descending order.
 * - Filter persistence: Maintains filter state until explicitly reset.
 *
 * Filter syntax examples:
 * - minPlayers>2: Games that support more than 2 players.
 * - maxTime<60: Games that take less than 60 minutes.
 * - name~=chess: Games with "chess" in their name.
 * - rating>=8: Games rated 8 or higher.
 *
 * Sort examples:
 * - sort:name:asc - Sort by name in ascending order.
 * - sort:rating:desc - Sort by rating in descending order.
 * - sort:year - Sort by year (default ascending).
 *
 * @author Yuchen Huang
 * @version 1.0
 */
public class Planner implements IPlanner {
    /** List of all board games in the collection. */
    private Set<BoardGame> allGames;
    
    /** List of currently filtered board games. */
    private List<BoardGame> filteredGames;

    /**
     * Constructor for Planner.
     * Initializes the planner with a set of board games.
     *
     * @param games the set of board games to manage
     */
    public Planner(Set<BoardGame> games) {
        this.allGames = new HashSet<>(games);
        this.filteredGames = new ArrayList<>();
    }

    /**
     * Filters the list of board games based on the provided filter string.
     * Uses default sorting by name in ascending order.
     *
     * @param filter the filter string to apply
     * @return a stream of filtered board games
     */
    @Override
    public Stream<BoardGame> filter(String filter) {
        return filter(filter, GameData.NAME, true);
    }

    /**
     * Filters the list of board games based on the provided filter string and sort criteria.
     * Uses default ascending order for sorting.
     *
     * @param filter the filter string to apply
     * @param sortOn the GameData field to sort on
     * @return a stream of filtered and sorted board games
     */
    @Override
    public Stream<BoardGame> filter(String filter, GameData sortOn) {
        return filter(filter, sortOn, true);
    }

    /**
     * Filters the list of board games based on the provided filter string, sort criteria, and order.
     * This is the main filtering method that implements the full filtering and sorting functionality.
     *
     * @param filter the filter string to apply
     * @param sortOn the GameData field to sort on
     * @param ascending whether to sort in ascending order
     * @return a stream of filtered and sorted board games
     */
    @Override
    public Stream<BoardGame> filter(String filter, GameData sortOn, boolean ascending) {
        // 每次过滤时都重新初始化过滤列表
        filteredGames = new ArrayList<>(allGames);

        // Apply filters if there are any
        if (!filter.isEmpty()) {
            String[] conditions = filter.split(",");
            for (String condition : conditions) {
                Filter parsedFilter = Filter.parseCondition(condition.trim());
                if (parsedFilter != null) {
                    filteredGames = filteredGames.stream()
                            .filter(parsedFilter::apply)
                            .collect(Collectors.toList());
                }
            }
        }

        // Sort the results
        Comparator<BoardGame> comparator = createComparator(sortOn);
        if (!ascending) {
            comparator = comparator.reversed();
        }

        return filteredGames.stream().sorted(comparator);
    }

    /**
     * Resets the filtered list of board games to the original list.
     * This clears any previously applied filters.
     */
    @Override
    public void reset() {
        filteredGames.clear();
    }

    /**
     * Creates a comparator for sorting based on the specified GameData field.
     * Provides type-safe comparison for different game attributes.
     *
     * @param sortOn the GameData field to create a comparator for
     * @return a comparator for sorting board games
     */
    private Comparator<BoardGame> createComparator(GameData sortOn) {
        return switch (sortOn) {
            case NAME -> Comparator.comparing(BoardGame::getName, String.CASE_INSENSITIVE_ORDER);
            case YEAR -> Comparator.comparingInt(BoardGame::getYearPublished);
            case MAX_TIME -> Comparator.comparingInt(BoardGame::getMaxPlayTime);
            case MIN_TIME -> Comparator.comparingInt(BoardGame::getMinPlayTime);
            case DIFFICULTY -> Comparator.comparingDouble(BoardGame::getDifficulty);
            case RANK -> Comparator.comparingInt(BoardGame::getRank);
            case MAX_PLAYERS -> Comparator.comparingInt(BoardGame::getMaxPlayers);
            case MIN_PLAYERS -> Comparator.comparingInt(BoardGame::getMinPlayers);
            case RATING -> Comparator.comparingDouble(BoardGame::getRating);
            default ->
                // Default to sorting by name if unknown field
                Comparator.comparing(BoardGame::getName, String.CASE_INSENSITIVE_ORDER);
        };
    }
}
