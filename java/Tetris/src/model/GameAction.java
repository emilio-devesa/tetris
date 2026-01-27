package model;

/**
 * Enum for game actions.
 * Represents possible player actions on the tetromino.
 */
public enum GameAction {
    LEFT,      // Move piece left
    RIGHT,     // Move piece right
    DOWN,      // Move piece down (accelerate)
    ROTATE,    // Rotate piece
    DROP,      // Instant drop to bottom
    NONE       // No action (used for tick without input)
}
