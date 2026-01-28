package controller;

import model.*;
import engine.GameEngine;
import engine.GameDemo;
import view.GameView;
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
    private GameState state;
    private GameTimer timer;
    private boolean running;
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
        // Show main menu
        int choice = showMainMenu();
        
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
     * Shows the main menu and returns user choice.
     *
     * @return selected menu option
     */
    private int showMainMenu() {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║     TETRIS - Main Menu             ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ 1 - Play Game                      ║");
        System.out.println("║ 2 - View High Scores               ║");
        System.out.println("║ 3 - Watch Demo                     ║");
        System.out.println("║ 4 - Exit                           ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.print("Choose (1-4): ");

        try {
            Scanner scanner = new Scanner(System.in);
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * Starts a normal interactive game.
     */
    private void playGame() {
        // Select difficulty
        GameDifficulty difficulty = selectDifficulty();
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

        // Game over - record statistics
        recordGameStatistics();
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
        
        System.out.print("Press Enter to return to menu...");
        try {
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
        } catch (Exception e) {
            // Continue
        }
    }

    /**
     * Runs an automatic demo game.
     */
    private void runDemo() {
        System.out.println();
        GameDifficulty difficulty = selectDifficulty();
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
        view.renderStatistics(stats);

        System.out.print("Press Enter to return to menu...");
        try {
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
        } catch (Exception e) {
            // Continue
        }
    }

    /**
     * Prompts user to select game difficulty level.
     *
     * @return selected GameDifficulty
     */
    private GameDifficulty selectDifficulty() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║     SELECT DIFFICULTY              ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ 1 - EASY   (slowest, no multiplier)║");
        System.out.println("║ 2 - NORMAL (balanced gameplay)    ║");
        System.out.println("║ 3 - HARD   (faster, 1.5x bonus)  ║");
        System.out.println("║ 4 - EXTREME(fastest, 2.0x bonus) ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.print("Choose (1-4, default=2): ");

        try {
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    return GameDifficulty.EASY;
                case "3":
                    return GameDifficulty.HARD;
                case "4":
                    return GameDifficulty.EXTREME;
                case "2":
                default:
                    return GameDifficulty.NORMAL;
            }
        } catch (Exception e) {
            return GameDifficulty.NORMAL;
        }
    }

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
     */
    private void inputLoop() {
        Scanner scanner = new Scanner(System.in);
        long lastTick = System.currentTimeMillis();
        final long TICK_INTERVAL = 100; // milliseconds

        while (running && !state.isGameOver()) {
            long now = System.currentTimeMillis();
            GameAction action = GameAction.NONE;

            // Check for user input (non-blocking)
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim().toUpperCase();
                GameAction parsedAction = parseInput(input);

                if (parsedAction == null) {
                    // Special command like QUIT
                    if ("QUIT".equals(input)) {
                        running = false;
                        break;
                    }
                } else {
                    action = parsedAction;
                }
            }

            // Process game tick at regular intervals
            if (now - lastTick >= TICK_INTERVAL) {
                // Count pieces when board grows
                int boardSizeBefore = state.getBoard().getOccupiedCells().size();
                state = engine.tick(state, action);
                int boardSizeAfter = state.getBoard().getOccupiedCells().size();
                
                if (boardSizeAfter > boardSizeBefore) {
                    piecesPlaced++;
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
     *
     * @param input the user input string
     * @return corresponding GameAction, or null for special commands
     */
    private GameAction parseInput(String input) {
        switch (input) {
            case "LEFT":
                return GameAction.LEFT;
            case "RIGHT":
                return GameAction.RIGHT;
            case "DOWN":
                return GameAction.DOWN;
            case "ROTATE":
                return GameAction.ROTATE;
            case "DROP":
                return GameAction.DROP;
            case "PAUSE":
                return GameAction.PAUSE;
            case "QUIT":
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
