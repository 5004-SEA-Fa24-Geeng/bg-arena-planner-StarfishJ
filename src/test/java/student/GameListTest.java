package student;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for GameList implementation.
 */
public class GameListTest {

    Set<BoardGame> games;

    @BeforeEach
    public void setup() {
        games = new HashSet<>();
        games.add(new BoardGame("17 days", 6, 1, 8, 30,60, 2.5, 150, 7.5, 2020));
        games.add(new BoardGame("Chess", 7, 2, 2, 15, 120, 3.0, 20, 8.8, 1850));
        games.add(new BoardGame("Go", 1, 2, 5, 20, 120, 4.0, 30, 8.0, 2000));
        games.add(new BoardGame("Go Fish", 2, 2, 10, 10, 30, 1.0, 300, 6.2, 1995));
        games.add(new BoardGame("golang", 4, 2, 7, 25, 45, 2.0, 250, 7.0, 2018));
        games.add(new BoardGame("GoRami", 3, 6, 6, 40, 80, 2.5, 180, 7.3, 2015));
        games.add(new BoardGame("Monopoly", 8, 6, 10, 60, 180, 2.5, 10, 7.5, 1935));
    }

    @Test
    public void testByAddSingleGameByIndex() {
        IGameList list = new GameList();
        list.addToList("1", games.stream());
        assertEquals(1, list.count());
        System.out.println(list.getGameNames());
    }

    @Test
    public void testAddRangeOfGames() {
        IGameList list = new GameList();
        list.addToList("1-3", games.stream());
        assertEquals(3, list.count());
        System.out.println(list.getGameNames());
    }

    @Test
    void getGameNames() {
        IGameList list = new GameList();
        list.addToList("all", games.stream());
        List<String> names = list.getGameNames();
        System.out.println("Game names: " + names);
        assertEquals(games.size(), names.size());
        for (BoardGame game : games) {
            assertTrue(names.contains(game.getName()));
        }
    }

    @Test
    void clear() {
        IGameList list = new GameList();
        list.addToList("1", games.stream());
        list.clear();
        assertEquals(0, list.count());
    }

    @Test
    void count() {
        IGameList list = new GameList();
        list.addToList("1", games.stream());
        list.addToList("2", games.stream());
        list.addToList("3", games.stream());
        assertEquals(3, list.count());
        list.clear();
        assertEquals(0, list.count());
    }

    @Test
    void addToList() {
        IGameList list = new GameList();
        list.addToList("1", games.stream());
        assertEquals(1, list.count());
    }

    @Test
    void removeFromList() {
        IGameList list = new GameList();
        list.addToList("1", games.stream());
        list.addToList("2", games.stream());
        list.addToList("3", games.stream());
        list.removeFromList("3");
        assertEquals(2, list.count());
    }

    @Test
    void testAddByName() {
        IGameList list = new GameList();
        list.addToList("Go Fish", games.stream());
        assertEquals(List.of("Go Fish"), list.getGameNames());
    }

    @Test
    void testAddByNameCaseInsensitive() {
        IGameList list = new GameList();
        list.addToList("go fish", games.stream());
        assertEquals(List.of("Go Fish"), list.getGameNames());
    }

    @Test
    void testAddByNameWithSpaces() {
        IGameList list = new GameList();
        list.addToList("  Go Fish  ", games.stream());
        assertEquals(List.of("Go Fish"), list.getGameNames());
    }

    @Test
    void testAddAllGames() {
        IGameList list = new GameList();
        list.addToList("all", games.stream());
        assertEquals(games.size(), list.count());
    }

    @Test
    void testInvalidIndex() {
        IGameList list = new GameList();
        assertThrows(IllegalArgumentException.class, () -> 
            list.addToList("999", games.stream()));
    }

    @Test
    void testInvalidRange() {
        IGameList list = new GameList();
        assertThrows(IllegalArgumentException.class, () -> 
            list.addToList("3-1", games.stream()));
    }

    @Test
    void testDuplicateAdd() {
        IGameList list = new GameList();
        list.addToList("1", games.stream());
        list.addToList("1", games.stream());
        assertEquals(1, list.count());
    }
}