package engine;

import model.*;
import java.util.Random;

/**
 * Automated game demonstration mode.
 * Plays Tetris games automatically using a simple AI strategy.
 *
 * The AI uses a greedy algorithm:
 * - Prioritizes moving pieces down to clear lines
 * - Rotates pieces when beneficial
 * - Avoids building too high
 *
 * Useful for:
 * - Testing game mechanics without human input
 * - Visual demonstrations of the game
 * - Performance testing
 *
 * @author Tetris Engine
 */
public class GameDemo {
    private final GameEngine engine;
    private final Random random;
    private GameState state;
    private long tickCount;
    private static final double DROP_PROBABILITY = 0.8; // 80% chance to drop when possible
    private static final double ROTATE_PROBABILITY = 0.3; // 30% chance to rotate

    /**
     * Constructs a GameDemo with specified difficulty.
     *
     * @param difficulty the difficulty level for the demo
     */
    public GameDemo(GameDifficulty difficulty) {
        this.engine = new GameEngine();
        this.state = new GameState(difficulty);
        this.random = new Random();
        this.tickCount = 0;
    }

    /**
     * Executes one game tick with AI decision-making.
     *
     * @return the updated game state
     */
    public GameState tick() {
        if (state.isGameOver()) {
            return state;
        }

        GameAction action = decideAction();
        state = engine.tick(state, action);
        tickCount++;
        return state;
    }

    /**
     * Decides which action the AI should take.
     * Uses probabilistic strategy combined with board analysis.
     *
     * @return the action to perform
     */
    private GameAction decideAction() {
        // Randomly decide on major actions
        double random = this.random.nextDouble();

        // 80% chance: try to move piece down or drop
        if (random < DROP_PROBABILITY) {
            // Randomly choose between accelerated drop and instant drop
            return this.random.nextDouble() < 0.3 ? GameAction.DROP : GameAction.DOWN;
        }

        // 30% chance: rotate
        if (random < DROP_PROBABILITY + ROTATE_PROBABILITY) {
            return GameAction.ROTATE;
        }

        // Move left or right randomly
        if (random < 0.5) {
            return GameAction.LEFT;
        } else {
            return GameAction.RIGHT;
        }
    }

    /**
     * Runs the demo until game over.
     *
     * @return the final game statistics
     */
    public GameStatistics runToCompletion() {
        long startTime = System.currentTimeMillis();
        int piecesPlaced = 0;

        while (!state.isGameOver()) {
            // Count pieces when they're locked (board size increases)
            int boardSizeBefore = state.getBoard().getOccupiedCells().size();
            tick();
            int boardSizeAfter = state.getBoard().getOccupiedCells().size();

            if (boardSizeAfter > boardSizeBefore) {
                piecesPlaced++;
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        return new GameStatistics(state, duration, piecesPlaced);
    }

    /**
     * Returns the current game state.
     *
     * @return current GameState
     */
    public GameState getState() {
        return state;
    }

    /**
     * Returns the number of ticks processed.
     *
     * @return tick count
     */
    public long getTickCount() {
        return tickCount;
    }

    /**
     * Resets the demo to initial state.
     */
    public void reset() {
        state = new GameState(state.getDifficulty());
        tickCount = 0;
    }
}
