package student;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * GameList implements the IGameList interface and provides functionality for managing
 * a collection of board games. This class serves as a core component of the Board Game
 * Arena Planner, allowing users to maintain and manipulate their list of games.
 *
 * Key features include:
 * - Adding games individually or in ranges
 * - Removing games from the list
 * - Saving the game list to a file
 * - Maintaining unique entries (no duplicates)
 * - Case-insensitive operations
 * - Automatic sorting of game names
 *
 * The class supports various ways to add games:
 * - By exact name
 * - By index number
 * - By range (e.g., "1-5")
 * - All games from a filtered list
 *
 * Example usage:
 * - add "Chess" - adds a specific game
 * - add "1-3" - adds games at positions 1 through 3
 * - add "all" - adds all games from the current filter
 * - remove "Chess" - removes a specific game
 * - save "mylist.txt" - saves the current list to a file
 *
 * @author Yuchen Huang
 * @version 1.0
 */
public class GameList implements IGameList {
    /** The set of board games in the list. */
    private Set<BoardGame> gameList;

    /**
     * Constructor for the GameList.
     */
    public GameList() {
        this.gameList = new HashSet<>();
    }

    /**
     * gets the list of board games.
     * @return List<BoardGame>: list of board games
     */
    @Override
    public List<String> getGameNames() {
        return gameList.stream()
                .map(BoardGame::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    /**
     * clear the game list.
     */
    @Override
    public void clear() {
        gameList.clear();
    }

    /**
     * Returns number of board games in the game list.
     * @return int: number of board games
     */
    @Override
    public int count() {
        return gameList.size();
    }

    /**
     * Saves the game list to a file.
     * @param filename the name of the file to save to
     */
    @Override
    public void saveGame(String filename) {
        try {
            // Create parent directories if they don't exist
            java.io.File file = new java.io.File(filename);
            java.io.File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                List<String> sortedNames = getGameNames();
                for (String gameName : sortedNames) {
                    writer.write(gameName);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving game list: " + e.getMessage());
            throw new RuntimeException("Failed to save game list: " + e.getMessage());
        }
    }

    /**
     * Adds a game to the list based on the provided string.
     * @param str the string representing the game to add
     * @param filtered the stream of filtered board games
     */
    @Override
    public void addToList(String str, Stream<BoardGame> filtered) throws IllegalArgumentException {
        List<BoardGame> filteredList = filtered.collect(Collectors.toList());

        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty.");
        }

        if (str.equalsIgnoreCase(ADD_ALL)) {
            gameList.addAll(filteredList);
            return;
        }

        // First try to match by name (case-insensitive)
        String trimmedStr = str.trim();
        List<BoardGame> matchedByName = filteredList.stream()
                .filter(game -> game.getName().equalsIgnoreCase(trimmedStr))
                .collect(Collectors.toList());

        if (!matchedByName.isEmpty()) {
            gameList.addAll(matchedByName);
            return;
        }

        // If no name match, try to parse as number or range
        try {
            if (trimmedStr.contains("-")) {
                String[] parts = trimmedStr.split("-");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid range format: " + trimmedStr);
                }
                int start = Integer.parseInt(parts[0].trim()) - 1;
                int end = Integer.parseInt(parts[1].trim());
                
                if (start < 0 || end > filteredList.size() || start >= end) {
                    throw new IllegalArgumentException(
                        String.format("Invalid range: %d-%d. Valid range is 1-%d", 
                            start + 1, end, filteredList.size()));
                }
                
                List<BoardGame> subList = filteredList.subList(start, end);
                gameList.addAll(new ArrayList<>(subList));
            } else {
                int index = Integer.parseInt(trimmedStr) - 1;
                if (index < 0 || index >= filteredList.size()) {
                    throw new IllegalArgumentException(
                        String.format("Invalid index: %d. Valid range is 1-%d", 
                            index + 1, filteredList.size()));
                }
                gameList.add(filteredList.get(index));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                String.format("'%s' is not a valid game name or number", trimmedStr));
        }
    }

    /**
     * Removes a game from the list based on the provided string.
     * @param str the string representing the game to remove
     */
    @Override
    public void removeFromList(String str) {
        if (str.equalsIgnoreCase(ADD_ALL)) {
            clear();
            return;
        }

        if (str.trim().isEmpty()) {
            System.out.println("Input cannot be empty.");
            return;
        }

        List<BoardGame> sortedList = new ArrayList<>(gameList);
        try {
            if (str.contains("-")) {
                String[] parts = str.split("-");
                int start = Integer.parseInt(parts[0]);
                int end = Integer.parseInt(parts[1]);
                if (start < 1 || end > sortedList.size() || start > end) {
                    throw new IllegalArgumentException("Invalid range. "
                            + "Please enter numbers between 1 and " + sortedList.size());
                }
                gameList.removeAll(sortedList.subList(start - 1, end));
            } else {
                // Check if input is a number (existing index-based removal)
                try {
                    int index = Integer.parseInt(str) - 1;
                    if (index < 0 || index >= sortedList.size()) {
                        throw new IllegalArgumentException();
                    }
                    gameList.remove(sortedList.get(index));
                } catch (NumberFormatException e) {
                    // If not a number, treat as game name
                    boolean removed = gameList.removeIf(game ->
                            game.getName().equalsIgnoreCase(str));

                    if (!removed) {
                        throw new IllegalArgumentException("Game not found in list: " + str);
                    }
                }
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input format: " + str);
        }
    }

}

