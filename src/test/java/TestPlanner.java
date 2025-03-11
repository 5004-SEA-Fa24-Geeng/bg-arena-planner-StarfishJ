import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import student.BoardGame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import student.Planner;
import student.IPlanner;
import student.GameData;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * JUnit test for the Planner class.
 * 
 * Just a sample test to get you started, also using
 * setup to help out. 
 */
public class TestPlanner {
    static Set<BoardGame> games;

    @BeforeEach
    public void setup() {
        games = new HashSet<>();
        games.add(new BoardGame("17 days", 6, 1, 8, 70, 70, 9.0, 600, 9.0, 2005));
        games.add(new BoardGame("Chess", 7, 2, 2, 10, 20, 10.0, 700, 10.0, 2006));
        games.add(new BoardGame("Go", 1, 2, 5, 30, 30, 8.0, 100, 7.5, 2000));
        games.add(new BoardGame("Go Fish", 2, 2, 10, 20, 120, 3.0, 200, 6.5, 2001));
        games.add(new BoardGame("golang", 4, 2, 7, 50, 55, 7.0, 400, 9.5, 2003));
        games.add(new BoardGame("GoRami", 3, 6, 6, 40, 42, 5.0, 300, 8.5, 2002));
        games.add(new BoardGame("Monopoly", 8, 6, 10, 20, 1000, 1.0, 800, 5.0, 2007));
        games.add(new BoardGame("Tucano", 5, 10, 20, 60, 90, 6.0, 500, 8.0, 2004));
    }

     @Test
    public void testFilterName() {
        IPlanner planner = new Planner(games);
        List<BoardGame> filtered = planner.filter("name == Go").toList();
        assertEquals(1, filtered.size());
        assertEquals("Go", filtered.get(0).getName());
    }
    
    @Test
    public void testChainedFilters() {
        IPlanner planner = new Planner(games);
        List<BoardGame> filtered = planner.filter("minPlayers>=2,maxPlayers<=6").toList();
        assertTrue(filtered.stream().allMatch(game -> 
            game.getMinPlayers() >= 2 && game.getMaxPlayers() <= 6));
    }

    @Test
    public void testSortingByRating() {
        IPlanner planner = new Planner(games);
        List<BoardGame> sorted = planner.filter("", GameData.RATING, false).toList();
        for (int i = 0; i < sorted.size() - 1; i++) {
            assertTrue(sorted.get(i).getRating() >= sorted.get(i + 1).getRating());
        }
    }

    @Test
    public void testSortingByName() {
        IPlanner planner = new Planner(games);
        List<BoardGame> sorted = planner.filter("", GameData.NAME, true).toList();
        for (int i = 0; i < sorted.size() - 1; i++) {
            assertTrue(sorted.get(i).getName().compareToIgnoreCase(
                sorted.get(i + 1).getName()) <= 0);
        }
    }

    @Test
    public void testComplexFilter() {
        IPlanner planner = new Planner(games);
        List<BoardGame> filtered = planner.filter(
            "minPlayers>=2,maxPlayers<=10,rating>=7.0").toList();
        assertTrue(filtered.stream().allMatch(game -> 
            game.getMinPlayers() >= 2 && 
            game.getMaxPlayers() <= 10 && 
            game.getRating() >= 7.0));
    }

    @Test
    public void testEmptyFilter() {
        IPlanner planner = new Planner(games);
        List<BoardGame> all = planner.filter("").toList();
        assertEquals(games.size(), all.size());
    }

    @Test
    public void testResetFilter() {
        IPlanner planner = new Planner(games);
        planner.filter("minPlayers>5").toList();
        planner.reset();
        List<BoardGame> all = planner.filter("").toList();
        assertEquals(games.size(), all.size());
    }

    @Test
    public void testNameContainsFilter() {
        IPlanner planner = new Planner(games);
        List<BoardGame> filtered = planner.filter("name~=Go").toList();
        assertTrue(filtered.stream().allMatch(game -> 
            game.getName().toLowerCase().contains("go")));
    }

    @Test
    public void testMultipleNameFilters() {
        IPlanner planner = new Planner(games);
        List<BoardGame> filtered = planner.filter("name>=go,name<=go").toList();
        assertEquals(1, filtered.size());
        assertEquals("Go", filtered.get(0).getName());
    }
}