package model;

/**
 * Immutable statistics tracker for a Tetris game session.
 * Records detailed game metrics for analysis and display.
 *
 * Tracks:
 * - Score and lines cleared
 * - Game duration and tick count
 * - Difficulty level and performance metrics
 *
 * @author Tetris Engine
 */
public class GameStatistics {
    private final int finalScore;
    private final int totalLinesCleared;
    private final long totalTicks;
    private final GameDifficulty difficulty;
    private final long playDurationMillis;
    private final int piecesPlaced;

    /**
     * Constructs GameStatistics from a final game state.
     *
     * @param finalState      the game state at the end of play
     * @param playDurationMs  the total time spent playing in milliseconds
     * @param piecesPlaced    the number of pieces placed during the game
     */
    public GameStatistics(GameState finalState, long playDurationMs, int piecesPlaced) {
        this.finalScore = finalState.getScore();
        this.totalLinesCleared = finalState.getLinesCleared();
        this.totalTicks = finalState.getTickCount();
        this.difficulty = finalState.getDifficulty();
        this.playDurationMillis = playDurationMs;
        this.piecesPlaced = piecesPlaced;
    }

    /**
     * Returns the final score achieved in the game.
     *
     * @return final score
     */
    public int getFinalScore() {
        return finalScore;
    }

    /**
     * Returns the total number of lines cleared.
     *
     * @return lines cleared
     */
    public int getTotalLinesCleared() {
        return totalLinesCleared;
    }

    /**
     * Returns the total number of game ticks processed.
     *
     * @return tick count
     */
    public long getTotalTicks() {
        return totalTicks;
    }

    /**
     * Returns the difficulty level the game was played on.
     *
     * @return difficulty level
     */
    public GameDifficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Returns the total play duration in milliseconds.
     *
     * @return play duration in milliseconds
     */
    public long getPlayDurationMillis() {
        return playDurationMillis;
    }

    /**
     * Returns the play duration in seconds.
     *
     * @return play duration in seconds
     */
    public double getPlayDurationSeconds() {
        return playDurationMillis / 1000.0;
    }

    /**
     * Returns the number of pieces placed during the game.
     *
     * @return pieces placed
     */
    public int getPiecesPlaced() {
        return piecesPlaced;
    }

    /**
     * Calculates the average score per line cleared.
     *
     * @return score per line, or 0 if no lines cleared
     */
    public double getScorePerLine() {
        return totalLinesCleared > 0 ? (double) finalScore / totalLinesCleared : 0;
    }

    /**
     * Calculates the average score per piece placed.
     *
     * @return score per piece, or 0 if no pieces placed
     */
    public double getScorePerPiece() {
        return piecesPlaced > 0 ? (double) finalScore / piecesPlaced : 0;
    }

    /**
     * Calculates the average game speed in ticks per second.
     *
     * @return ticks per second
     */
    public double getTicksPerSecond() {
        return getPlayDurationSeconds() > 0 ? totalTicks / getPlayDurationSeconds() : 0;
    }

    /**
     * Returns a formatted string representation of statistics.
     *
     * @return formatted statistics
     */
    public String getFormattedSummary() {
        return String.format(
            "Final Score: %d | Lines: %d | Pieces: %d | Duration: %.1fs | Difficulty: %s",
            finalScore, totalLinesCleared, piecesPlaced, getPlayDurationSeconds(), difficulty
        );
    }

    @Override
    public String toString() {
        return getFormattedSummary();
    }
}
