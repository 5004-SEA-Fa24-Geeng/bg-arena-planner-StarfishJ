package student;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * GamesLoader provides functionality for loading board games from CSV files.
 * This utility class handles the parsing and conversion of CSV data into BoardGame objects.
 *
 * Key features:
 * - CSV file parsing with comma delimiter
 * - Header row processing for column mapping
 * - Data validation and error handling
 * - Conversion of string data to appropriate types
 *
 * The class assumes:
 * - CSV files are properly formatted with no commas in data fields
 * - First row contains headers matching GameData enum values
 * - All required columns are present in the file
 *
 * @author Yuchen Huang
 * @version 1.0
 */
public final class GamesLoader {
    /** Standard CSV delimiter character. */
    private static final String DELIMITER = ",";

    /** Private constructor to prevent instantiation of utility class. */
    private GamesLoader() {
    }

    /**
     * Loads board games from a CSV file into a set of BoardGame objects.
     * The file should be located in the resources directory.
     *
     * @param filename the name of the file to load (relative to resources directory)
     * @return a set of BoardGame objects created from the file data
     * @throws RuntimeException if there is an error reading or parsing the file
     */
    public static Set<BoardGame> loadGamesFile(String filename) {
        Set<BoardGame> games = new HashSet<>();
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        
        try {
            // 尝试多种方式加载资源文件
            is = GamesLoader.class.getResourceAsStream("/" + filename);
            if (is == null) {
                is = GamesLoader.class.getClassLoader().getResourceAsStream(filename);
            }
            if (is == null) {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
            }
            
            if (is == null) {
                System.err.println("Error: Could not find resource file: " + filename);
                return games;
            }
            
            isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            reader = new BufferedReader(isr);
            List<String> lines = reader.lines().collect(Collectors.toList());
            
            if (lines.isEmpty()) {
                return games;
            }

            Map<GameData, Integer> columnMap = processHeader(lines.remove(0));
            games = lines.stream()
                    .map(line -> toBoardGame(line, columnMap))
                    .filter(game -> game != null)
                    .collect(Collectors.toSet());
                    
        } catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
            e.printStackTrace(); 
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return games;
    }

    /**
     * Processes the header row of the CSV file to create a mapping of columns to their indices.
     *
     * @param headerLine the first line of the CSV file containing column headers
     * @return a map linking GameData enum values to their corresponding column indices
     */
    private static Map<GameData, Integer> processHeader(String headerLine) {
        Map<GameData, Integer> columnMap = new HashMap<>();
        String[] columns = headerLine.split(DELIMITER);
        for (int i = 0; i < columns.length; i++) {
            try {
                GameData col = GameData.fromColumnName(columns[i]);
                columnMap.put(col, i);
            } catch (IllegalArgumentException e) {
                // System.out.println("Ignoring column: " + columns[i]);
            }
        }
        return columnMap;
    }

    /**
     * Converts a line of CSV data into a BoardGame object.
     *
     * @param line the CSV line to parse
     * @param columnMap the mapping of GameData values to column indices
     * @return a new BoardGame object, or null if the data is invalid
     */
    private static BoardGame toBoardGame(String line, Map<GameData, Integer> columnMap) {
        String[] columns = line.split(DELIMITER);
        if (columns.length < columnMap.values().stream().max(Integer::compareTo).get()) {
            return null;
        }

        try {
            BoardGame game = new BoardGame(columns[columnMap.get(GameData.NAME)],
                    Integer.parseInt(columns[columnMap.get(GameData.ID)]),
                    Integer.parseInt(columns[columnMap.get(GameData.MIN_PLAYERS)]),
                    Integer.parseInt(columns[columnMap.get(GameData.MAX_PLAYERS)]),
                    Integer.parseInt(columns[columnMap.get(GameData.MIN_TIME)]),
                    Integer.parseInt(columns[columnMap.get(GameData.MAX_TIME)]),
                    Double.parseDouble(columns[columnMap.get(GameData.DIFFICULTY)]),
                    Integer.parseInt(columns[columnMap.get(GameData.RANK)]),
                    Double.parseDouble(columns[columnMap.get(GameData.RATING)]),
                    Integer.parseInt(columns[columnMap.get(GameData.YEAR)]));
            return game;
        } catch (NumberFormatException e) {
            // skip if there is an issue
            return null;
        }
    }

}
