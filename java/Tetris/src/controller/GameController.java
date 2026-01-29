package controller;

import model.*;
import engine.GameEngine;
import engine.GameDemo;
import view.GameView;
import view.Renderer;
import view.AudioManager;
import java.io.IOException;
import java.util.Scanner;

/**
 * Controller coordinating input handling with the game engine and view.
 * Manages the main game loop using separate threads for input and rendering.
 *
 * The GameController implements the MVC pattern:
 * - Model: GameState and GameEngine (pure logic)
 * - View: Renderer (presentation)
 * - Controller: GameController (input processing and orchestration)
 *
 * Features:
 * - Non-blocking input handling
 * - Separate render and input loops for responsive UI
 * - Difficulty selection before game starts
 * - High score tracking and persistence
 * - Game statistics collection
 * - Demo mode for automatic play
 * - Thread-safe state management
 */
public class GameController {
    private final GameEngine engine;
    private final GameView view;
    private final HighScoreManager highScoreManager;
    private final GameConfig config;
    private final AudioManager audioManager;
    private GameState state;
    private GameTimer timer;
    private volatile boolean running;
    private long gameStartTime;
    private int piecesPlaced;

    /**
     * Constructs a new GameController with a specific view implementation.
     *
     * @param view the GameView implementation (Terminal or Swing)
     */
    public GameController(GameView view) {
        this.engine = new GameEngine();
        this.view = view;
        this.highScoreManager = new HighScoreManager();
        this.config = new GameConfig();
        this.audioManager = new AudioManager();
        this.audioManager.setEnabled(true); // Enable audio by default
        this.state = new GameState();
        this.timer = new GameTimer();
        this.running = false;
        this.piecesPlaced = 0;
    }

    /**
     * Starts an interactive game session.
     * Prompts for difficulty selection, then runs the game loop.
     */
    public void play() {
        // Show main menu using the view (terminal or GUI)
        int choice = view.showMainMenu();
        
        switch (choice) {
            case 1:
                playGame();
                break;
            case 2:
                showHighScores();
                play(); // Return to menu
                break;
            case 3:
                runDemo();
                play(); // Return to menu
                break;
            case 4:
                System.out.println("Thanks for playing Tetris!");
                System.exit(0);
            default:
                play();
        }
    }

    /**
     * Starts a normal interactive game.
     */
    private void playGame() {
        // Select difficulty using the view (terminal or GUI)
        GameDifficulty difficulty = view.selectDifficulty();
        
        // Select background music using the view (terminal or GUI)
        AudioManager.Soundtrack soundtrack = view.selectSoundtrack();
        audioManager.playMusic(soundtrack);
        
        state = new GameState(difficulty);
        gameStartTime = System.currentTimeMillis();
        piecesPlaced = 0;

        running = true;
        view.renderHelp();

        // Start game loop threads
        Thread renderThread = new Thread(this::renderLoop);
        Thread inputThread = new Thread(this::inputLoop);

        renderThread.start();
        inputThread.start();

        try {
            inputThread.join();
            running = false;
            renderThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Game over - render game over screen and statistics
        view.renderGameOver(state);
        recordGameStatistics();
        
        // Return to main menu automatically
        play();
    }

    /**
     * Records and displays game statistics after game over.
     */
    private void recordGameStatistics() {
        long duration = System.currentTimeMillis() - gameStartTime;
        GameStatistics stats = new GameStatistics(state, duration, piecesPlaced);

        view.renderStatistics(stats);

        boolean isHighScore = highScoreManager.recordScore(stats);
        if (isHighScore) {
            System.out.println("\n🎉 NEW HIGH SCORE! 🎉");
        }
    }

    /**
     * Displays high scores menu.
     */
    private void showHighScores() {
        System.out.println();
        view.renderHighScores(highScoreManager.getTopScores());
        
        // Only prompt for input in terminal mode (Renderer)
        if (view instanceof Renderer) {
            System.out.print("Press Enter to return to menu...");
            try (Scanner scanner = new Scanner(System.in)) {
                scanner.nextLine();
            } catch (Exception e) {
                // Continue
            }
        }
    }

    /**
     * Runs an automatic demo game.
     */
    private void runDemo() {
        System.out.println();
        GameDifficulty difficulty = view.selectDifficulty();
        System.out.println("Starting demo mode. Press Ctrl+C to stop...");
        
        GameDemo demo = new GameDemo(difficulty);
        long startTime = System.currentTimeMillis();

        while (!demo.getState().isGameOver() && System.currentTimeMillis() - startTime < 60000) {
            // Run demo tick and render occasionally
            if (demo.getTickCount() % 10 == 0) {
                view.clearScreen();
                view.render(demo.getState());
            }
            demo.tick();

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }

        GameStatistics stats = demo.runToCompletion();
        view.renderGameOver(demo.getState());
        view.renderStatistics(stats);

        // Only prompt for input in terminal mode (Renderer)
        if (view instanceof Renderer) {
            System.out.print("Press Enter to return to menu...");
            try (Scanner scanner = new Scanner(System.in)) {
                scanner.nextLine();
            } catch (Exception e) {
                // Continue
            }
        }
        
        // Return to main menu automatically
        play();
    }

    /**
     * Prompts user to select game difficulty level.
     *
     * @return selected GameDifficulty
     */

    /**
     * Rendering loop - updates display at regular intervals (50ms).
     * Runs in a separate thread to maintain responsive UI.
     */
    private void renderLoop() {
        long lastRender = System.currentTimeMillis();
        final long RENDER_INTERVAL = 50; // milliseconds

        while (running) {
            long now = System.currentTimeMillis();
            if (now - lastRender >= RENDER_INTERVAL) {
                view.render(state);
                lastRender = now;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Input loop - reads user commands and processes game ticks.
     * Runs in a separate thread to allow non-blocking input.
     * Works with both terminal and GUI implementations.
     */
    private void inputLoop() {
        Scanner scanner = new Scanner(System.in);
        long lastTick = System.currentTimeMillis();
        final long TICK_INTERVAL = 100; // milliseconds

        while (running && !state.isGameOver()) {
            long now = System.currentTimeMillis();
            GameAction action = GameAction.NONE;

            // Get input from view (works for both terminal and GUI)
            GameAction inputAction = view.readInput();
            
            if (inputAction != null) {
                if (inputAction == GameAction.QUIT) {
                    running = false;
                    break;
                }
                action = inputAction;
            } else if (view instanceof Renderer) {
                // Terminal-specific fallback input handling with non-blocking check
                try {
                    if (System.in.available() > 0 && scanner.hasNextLine()) {
                        String input = scanner.nextLine().trim().toUpperCase();
                        GameAction parsedAction = parseInput(input);

                        if (parsedAction == GameAction.QUIT) {
                            running = false;
                            break;
                        } else if (parsedAction != null && parsedAction != GameAction.NONE) {
                            action = parsedAction;
                        }
                    }
                } catch (IOException e) {
                    // Continue if there's any input error
                }
            }

            // Process game tick at regular intervals
            if (now - lastTick >= TICK_INTERVAL) {
                // Track state before and after tick
                int boardSizeBefore = state.getBoard().getOccupiedCells().size();
                int linesBefore = state.getLinesCleared();
                
                state = engine.tick(state, action);
                
                int boardSizeAfter = state.getBoard().getOccupiedCells().size();
                int linesAfter = state.getLinesCleared();
                
                // Play sound when piece is placed
                if (boardSizeAfter > boardSizeBefore) {
                    piecesPlaced++;
                    audioManager.playSound(AudioManager.SoundEffect.PIECE_PLACED);
                }
                
                // Play sound when lines are cleared
                if (linesAfter > linesBefore) {
                    audioManager.playSound(AudioManager.SoundEffect.LINE_CLEARED);
                }
                
                lastTick = now;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        scanner.close();
        running = false;
    }

    /**
     * Parses user input string to GameAction enum.
     * Accepts both full commands (LEFT, RIGHT, etc.) and single-key shortcuts (A, D, S, W, R, P).
     *
     * @param input the user input string
     * @return corresponding GameAction, or null for special commands
     */
    private GameAction parseInput(String input) {
        if (input == null || input.isEmpty()) {
            return GameAction.NONE;
        }
        
        // Full command names
        switch (input) {
            case "LEFT":
            case "A":  // Shortcut for left
                return GameAction.LEFT;
            case "RIGHT":
            case "D":  // Shortcut for right
                return GameAction.RIGHT;
            case "DOWN":
            case "S":  // Shortcut for down
                return GameAction.DOWN;
            case "ROTATE":
            case "R":  // Shortcut for rotate
            case "W":  // Alternative shortcut for rotate (up)
                return GameAction.ROTATE;
            case "DROP":
            case "SPACE":
            case " ":  // Spacebar for drop
                return GameAction.DROP;
            case "PAUSE":
            case "P":  // Shortcut for pause
                return GameAction.PAUSE;
            case "QUIT":
            case "Q":  // Shortcut for quit
                return null; // Special case
            default:
                return GameAction.NONE;
        }
    }

    /**
     * Returns the current game state (useful for testing).
     *
     * @return the current GameState
     */
    public GameState getState() {
        return state;
    }

    /**
     * Advances the game by one tick with a specific action (useful for testing).
     *
     * @param action the action to perform this tick
     */
    public void tick(GameAction action) {
        if (!state.isGameOver()) {
            state = engine.tick(state, action);
        }
    }

    /**
     * Resets the game to initial state.
     */
    public void reset() {
        state = new GameState();
    }

    /**
     * Resets the game with a specific difficulty level.
     *
     * @param difficulty the difficulty level for the new game
     */
    public void resetWithDifficulty(GameDifficulty difficulty) {
        state = new GameState(difficulty);
    }

    /**
     * Returns the high score manager.
     *
     * @return the HighScoreManager instance
     */
    public HighScoreManager getHighScoreManager() {
        return highScoreManager;
    }

    /**
     * Returns the game configuration.
     *
     * @return the GameConfig instance
     */
    public GameConfig getConfig() {
        return config;
    }

    /**
     * Returns the game timer.
     *
     * @return the GameTimer instance
     */
    public GameTimer getTimer() {
        return timer;
    }
}
