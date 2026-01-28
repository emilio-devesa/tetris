package view;

import model.*;
import java.util.List;

/**
 * Common interface for all game view implementations (Terminal, Swing, etc).
 * Defines the contract between the game controller and rendering backend.
 *
 * Implementations:
 * - TerminalRenderer: Unicode terminal display
 * - SwingRenderer: GUI with Swing/AWT
 */
public interface GameView {
    /**
     * Renders the current game state.
     * Called approximately 20 times per second (50ms interval).
     *
     * @param state the current GameState to render
     */
    void render(GameState state);

    /**
     * Reads a single input action from the user.
     * Should be non-blocking or use appropriate timeout.
     *
     * @return the GameAction input, or null if no input
     */
    GameAction readInput();

    /**
     * Closes/cleans up the view resources.
     * Called when the game ends or on shutdown.
     */
    void close();

    /**
     * Renders game over screen with final statistics.
     *
     * @param state the final game state
     */
    void renderGameOver(GameState state);

    /**
     * Renders a list of high scores.
     *
     * @param topScores list of high scores to display
     */
    void renderHighScores(List<HighScoreManager.HighScore> topScores);

    /**
     * Renders game statistics summary.
     *
     * @param statistics the statistics to display
     */
    void renderStatistics(GameStatistics statistics);

    /**
     * Renders help message with game controls.
     */
    void renderHelp();

    /**
     * Clears the screen/terminal.
     */
    void clearScreen();

    /**
     * Shows the main menu and returns the user's choice.
     * Implementation depends on the view type (terminal or GUI).
     *
     * @return menu choice (1-4)
     */
    int showMainMenu();

    /**
     * Shows difficulty selection dialog and returns the selected difficulty.
     * Implementation depends on the view type (terminal or GUI).
     *
     * @return selected GameDifficulty
     */
    GameDifficulty selectDifficulty();}