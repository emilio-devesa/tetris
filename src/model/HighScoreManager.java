package model;

import java.io.*;
import java.util.*;

/**
 * Manager for high scores persistence.
 * Handles loading, saving, and ranking high scores from/to file system.
 *
 * High scores are stored in a JSON-like format for easy readability.
 * Maintains up to 10 top scores by difficulty level.
 *
 * @author Tetris Engine
 */
public class HighScoreManager {
    private static final String HIGHSCORES_DIR = "highscores";
    private static final String HIGHSCORES_FILE = "highscores/tetris_scores.txt";
    private static final int MAX_SCORES = 10;

    private final List<HighScore> scores;

    /**
     * Constructs a HighScoreManager and loads existing scores from disk.
     */
    public HighScoreManager() {
        this.scores = new ArrayList<>();
        loadScores();
    }

    /**
     * Records a new game score and persists it if it qualifies for top 10.
     *
     * @param statistics the game statistics to record
     * @return true if score qualifies for top 10, false otherwise
     */
    public boolean recordScore(GameStatistics statistics) {
        HighScore newScore = new HighScore(
            statistics.getFinalScore(),
            statistics.getDifficulty(),
            statistics.getTotalLinesCleared(),
            System.currentTimeMillis()
        );

        scores.add(newScore);
        scores.sort(Comparator.reverseOrder());

        // Keep only top 10
        if (scores.size() > MAX_SCORES) {
            scores.subList(MAX_SCORES, scores.size()).clear();
        }

        boolean qualified = scores.contains(newScore) && scores.indexOf(newScore) < MAX_SCORES;

        if (qualified) {
            saveScores();
        }

        return qualified;
    }

    /**
     * Returns the top high scores.
     *
     * @return list of top high scores
     */
    public List<HighScore> getTopScores() {
        return new ArrayList<>(scores);
    }

    /**
     * Returns the top score overall.
     *
     * @return the highest score, or null if no scores recorded
     */
    public HighScore getTopScore() {
        return scores.isEmpty() ? null : scores.get(0);
    }

    /**
     * Returns the top scores for a specific difficulty level.
     *
     * @param difficulty the difficulty level to filter
     * @return list of top scores for that difficulty
     */
    public List<HighScore> getTopScoresForDifficulty(GameDifficulty difficulty) {
        List<HighScore> filtered = new ArrayList<>();
        for (HighScore score : scores) {
            if (score.getDifficulty() == difficulty) {
                filtered.add(score);
            }
        }
        return filtered;
    }

    /**
     * Loads high scores from disk.
     */
    private void loadScores() {
        File file = new File(HIGHSCORES_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                HighScore score = HighScore.parse(line);
                if (score != null) {
                    scores.add(score);
                }
            }
            scores.sort(Comparator.reverseOrder());
        } catch (IOException e) {
            System.err.println("Warning: Could not load high scores: " + e.getMessage());
        }
    }

    /**
     * Saves high scores to disk.
     */
    private void saveScores() {
        try {
            File dir = new File(HIGHSCORES_DIR);
            dir.mkdirs();

            try (PrintWriter writer = new PrintWriter(new FileWriter(HIGHSCORES_FILE))) {
                for (HighScore score : scores) {
                    writer.println(score.serialize());
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not save high scores: " + e.getMessage());
        }
    }

    /**
     * Clears all high scores.
     */
    public void clearScores() {
        scores.clear();
        File file = new File(HIGHSCORES_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Inner class representing a single high score entry.
     */
    public static class HighScore implements Comparable<HighScore> {
        private final int score;
        private final GameDifficulty difficulty;
        private final int lines;
        private final long timestamp;

        /**
         * Constructs a high score entry.
         */
        public HighScore(int score, GameDifficulty difficulty, int lines, long timestamp) {
            this.score = score;
            this.difficulty = difficulty;
            this.lines = lines;
            this.timestamp = timestamp;
        }

        public int getScore() {
            return score;
        }

        public GameDifficulty getDifficulty() {
            return difficulty;
        }

        public int getLines() {
            return lines;
        }

        public long getTimestamp() {
            return timestamp;
        }

        /**
         * Serializes to string format for file storage.
         */
        public String serialize() {
            return String.format("%d|%s|%d|%d", score, difficulty.name(), lines, timestamp);
        }

        /**
         * Parses a high score from serialized string.
         */
        public static HighScore parse(String line) {
            try {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    int score = Integer.parseInt(parts[0]);
                    GameDifficulty difficulty = GameDifficulty.valueOf(parts[1]);
                    int lines = Integer.parseInt(parts[2]);
                    long timestamp = Long.parseLong(parts[3]);
                    return new HighScore(score, difficulty, lines, timestamp);
                }
            } catch (Exception e) {
                // Ignore malformed lines
            }
            return null;
        }

        @Override
        public int compareTo(HighScore other) {
            return Integer.compare(this.score, other.score);
        }

        @Override
        public String toString() {
            return String.format("Score: %d | %s | Lines: %d", score, difficulty, lines);
        }
    }
}
