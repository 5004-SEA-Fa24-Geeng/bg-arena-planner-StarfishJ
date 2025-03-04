package student;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Planner class to manage and filter board games.
 */
public class Planner implements IPlanner {
    // List of all board games
    private Set<BoardGame> allGames;
    // List of filtered board games
    private List<BoardGame> filteredGames;

    /**
     * Constructor for Planner.
     * @param games the set of board games to manage
     */
    public Planner(Set<BoardGame> games) {
        this.allGames = new HashSet<>(games);
        this.filteredGames = new ArrayList<>();
    }

    /**
     * filters the list of board games based on the provided filter string.
     * @param filter the filter string to apply
     * @return a stream of filtered board games
     */
    @Override
    public Stream<BoardGame> filter(String filter) {
        return filter(filter, GameData.NAME, true);
    }

    /**
     * filters the list of board games based on the provided filter string and sort criteria.
     * @param filter the filter string to apply
     * @param sortOn the GameData field to sort on
     * @return a stream of filtered and sorted board games
     */
    @Override
    public Stream<BoardGame> filter(String filter, GameData sortOn) {
        return filter(filter, sortOn, true);
    }

    /**
     * filters the list of board games based on the provided filter string, sort criteria, and order.
     * @param filter the filter string to apply
     * @param sortOn the GameData field to sort on
     * @param ascending whether to sort in ascending order
     * @return a stream of filtered and sorted board games
     */
    @Override
    public Stream<BoardGame> filter(String filter, GameData sortOn, boolean ascending) {
        if (filteredGames.isEmpty()) {
            filteredGames = new ArrayList<>(allGames);
        }

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
     */
    @Override
    public void reset() {
        filteredGames.clear();
    }

    /**
     * Creates a comparator for sorting based on the specified GameData field
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