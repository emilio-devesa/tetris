package model;

/**
 * Enumeration of game difficulty levels.
 * Each level affects gravity speed and score multiplier.
 */
public enum GameDifficulty {
    /**
     * Easy: Slowest gravity, pieces fall more slowly
     * Gravity ticks: 15
     * Score multiplier: 1.0x
     */
    EASY(15, 1.0f),

    /**
     * Normal: Standard gravity, balanced gameplay
     * Gravity ticks: 10
     * Score multiplier: 1.0x
     */
    NORMAL(10, 1.0f),

    /**
     * Hard: Faster gravity, pieces fall quicker
     * Gravity ticks: 6
     * Score multiplier: 1.5x
     */
    HARD(6, 1.5f),

    /**
     * Extreme: Very fast gravity, pieces fall rapidly
     * Gravity ticks: 3
     * Score multiplier: 2.0x
     */
    EXTREME(3, 2.0f);

    private final int gravityTicks;
    private final float scoreMultiplier;

    /**
     * Constructs a GameDifficulty with specified gravity and score parameters.
     *
     * @param gravityTicks    number of ticks before piece falls one row
     * @param scoreMultiplier multiplier for score calculation
     */
    GameDifficulty(int gravityTicks, float scoreMultiplier) {
        this.gravityTicks = gravityTicks;
        this.scoreMultiplier = scoreMultiplier;
    }

    /**
     * Returns the number of ticks before gravity applies.
     *
     * @return gravity ticks for this difficulty level
     */
    public int getGravityTicks() {
        return gravityTicks;
    }

    /**
     * Returns the score multiplier for this difficulty level.
     *
     * @return score multiplier (e.g., 1.5 for 50% bonus)
     */
    public float getScoreMultiplier() {
        return scoreMultiplier;
    }
}
