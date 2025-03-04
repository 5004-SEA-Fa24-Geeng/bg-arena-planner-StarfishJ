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
 * Class representing a list of board games.
 */
public class GameList implements IGameList {
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
        return List.copyOf(gameList.stream().map(BoardGame::getName).collect(Collectors.toList()));
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String gameName : getGameNames()) {
                writer.write(gameName);
                writer.newLine();
            }
            System.out.println("Game list saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving game list: " + e.getMessage());
        }
    }

    /**
     * Adds a game to the list based on the provided string.
     * @param str the string representing the game to add
     * @param filtered the stream of filtered board games
     */
    @Override
    public void addToList(String str, Stream<BoardGame> filtered) {
        List<BoardGame> filteredList = filtered.toList();

        if (str.equalsIgnoreCase(ADD_ALL)) {
            gameList.addAll(filteredList);
            return;
        }

        if (str.trim().isEmpty()) {
            System.out.println("Input cannot be empty.");
            return;
        }

        // Check if the input is a valid game name in the filtered list
        List<BoardGame> matchedByName = filteredList.stream()
                .filter(game -> game.getName().equalsIgnoreCase(str))
                .toList();

        if (!matchedByName.isEmpty()) {
            gameList.addAll(matchedByName);
            return;
        }

        try {
            if (str.contains("-")) { // Handle range
                String[] parts = str.split("-");
                int start = Integer.parseInt(parts[0]);
                int end = Integer.parseInt(parts[1]);
                if (start < 1 || end > filteredList.size() || start > end)
                    throw new IllegalArgumentException("Invalid range. " +
                            "Please enter numbers between 1 and " + filteredList.size());
                gameList.addAll(filteredList.subList(start - 1, end));
            } else { // Single entry
                int index = Integer.parseInt(str) - 1;
                if (index < 0 || index >= filteredList.size()) throw new IllegalArgumentException();
                gameList.add(filteredList.get(index));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input format: " + str);
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
                if (start < 1 || end > sortedList.size() || start > end)
                    throw new IllegalArgumentException("Invalid range. " +
                            "Please enter numbers between 1 and " + sortedList.size());
                gameList.removeAll(sortedList.subList(start - 1, end));
            } else {
                // Check if input is a number (existing index-based removal)
                try {
                    int index = Integer.parseInt(str) - 1;
                    if (index < 0 || index >= sortedList.size()) throw new IllegalArgumentException();
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

