package model;

/**
 * Base exception for Tetris game errors.
 * All game-specific exceptions inherit from this class.
 */
public class GameException extends Exception {
    /**
     * Constructs a GameException with the specified detail message.
     *
     * @param message the detail message
     */
    public GameException(String message) {
        super(message);
    }

    /**
     * Constructs a GameException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
}
