package student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilterTest {
    private BoardGame testGame;

    @BeforeEach
    void setUp(){
        //BoardGame(String name, int id, int minPlayers, int maxPlayers, int minPlayTime,
        //          int maxPlayTime, double difficulty, int rank, double averageRating, int yearPublished)
        testGame = new BoardGame("Test Game", 2, 4, 10, 30,
                60, 2.0, 2, 1.3, 2020);
    } 
    @Test
    void testSetUp() {
        assertEquals(testGame.getName(), "Test Game");
        assertEquals(testGame.getId(), 2);
        assertEquals(testGame.getMinPlayers(),4);
        assertEquals(testGame.getMaxPlayers(),10);
        assertEquals(testGame.getMinPlayTime(), 30);
        assertEquals(testGame.getMaxPlayTime(), 60);
        assertEquals(testGame.getDifficulty(), 2.0);
        assertEquals(testGame. getRank(), 2);
        assertEquals(testGame.getRating(), 1.3);
        assertEquals(testGame.getYearPublished(), 2020);
    }
    @Test
    void testParseCondition() {
        Filter filter = Filter.parseCondition("minPlayers>2");
        assertNotNull(filter);
        assertTrue(filter.apply(testGame)); // minPlayers > 2

        filter = Filter.parseCondition("rating>=7.5");
        assertNotNull(filter);
        assertFalse(filter.apply(testGame)); // rating>=7.5
    }

    @Test
    void testInvalidCondition() {
        Filter filter = Filter.parseCondition("invalid condition");
        assertNull(filter);
    }

    @Test
    void testContainsOperator() {
        Filter filter = Filter.parseCondition("name~=Test");
        assertNotNull(filter);
        assertTrue(filter.apply(testGame));

        filter = Filter.parseCondition("name~=NotFound");
        assertNotNull(filter);
        assertFalse(filter.apply(testGame));
    }

}