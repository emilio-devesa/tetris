package controller;

import model.*;
import engine.GameEngine;
import view.Renderer;
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
 * - Thread-safe state management
 */
public class GameController {
    private final GameEngine engine;
    private final Renderer renderer;
    private GameState state;
    private boolean running;

    /**
     * Constructs a new GameController with default settings.
     */
    public GameController() {
        this.engine = new GameEngine();
        this.renderer = new Renderer();
        this.state = new GameState();
        this.running = false;
    }

    /**
     * Starts an interactive game session.
     * Prompts for difficulty selection, then runs the game loop.
     */
    public void play() {
        // Select difficulty
        GameDifficulty difficulty = selectDifficulty();
        state = new GameState(difficulty);

        running = true;
        renderer.renderHelp();

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
                renderer.render(state);
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
                state = engine.tick(state, action);
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
}
