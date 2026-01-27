package controller;

import model.*;
import engine.GameEngine;
import view.Renderer;
import java.util.Scanner;

/**
 * Controller that coordinates input handling with the game engine.
 * Manages the main game loop for interactive play.
 */
public class GameController {
    private final GameEngine engine;
    private final Renderer renderer;
    private GameState state;
    private boolean running;

    public GameController() {
        this.engine = new GameEngine();
        this.renderer = new Renderer();
        this.state = new GameState();
        this.running = false;
    }

    /**
     * Starts an interactive game session.
     */
    public void play() {
        running = true;
        renderer.renderHelp();
        
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
     * Rendering loop - updates display at regular intervals.
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
     * Input loop - reads user commands and processes ticks.
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
                action = parseInput(input);
                
                if (action == null) {
                    // Special command like QUIT
                    if ("QUIT".equals(input)) {
                        running = false;
                        break;
                    }
                    action = GameAction.NONE;
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
     * Parses user input to GameAction.
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
     */
    public GameState getState() {
        return state;
    }

    /**
     * Advances the game by one tick with a specific action (useful for testing).
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
}
