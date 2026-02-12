package model;

/**
 * Exception thrown when a piece movement or rotation cannot be executed.
 * Indicates collision detection or boundary violation.
 */
public class InvalidMoveException extends GameException {
    /**
     * Constructs an InvalidMoveException with the specified detail message.
     *
     * @param message the detail message describing the invalid move
     */
    public InvalidMoveException(String message) {
        super(message);
    }

    /**
     * Constructs an InvalidMoveException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public InvalidMoveException(String message, Throwable cause) {
        super(message, cause);
    }
}
