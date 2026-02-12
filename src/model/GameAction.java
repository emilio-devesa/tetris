package model;

/**
 * Enumeration of possible player actions in the Tetris game.
 * Represents the input commands that can be performed on the falling tetromino.
 *
 * Actions are processed each game tick. If no input is provided, NONE is used.
 *
 * @author Tetris Engine
 */
public enum GameAction {
    /**
     * Move the piece one column to the left.
     * Blocked if piece touches the left boundary or fixed pieces.
     */
    LEFT,

    /**
     * Move the piece one column to the right.
     * Blocked if piece touches the right boundary or fixed pieces.
     */
    RIGHT,

    /**
     * Move the piece one row down (accelerate).
     * If blocked, piece locks and new piece spawns.
     */
    DOWN,

    /**
     * Rotate the piece clockwise.
     * Supports wall kick: automatic shift left/right if rotation blocked at boundary.
     * If wall kick fails, rotation is ignored.
     */
    ROTATE,

    /**
     * Instantly drop the piece to the bottom.
     * Piece locks at the lowest possible position.
     */
    DROP,

    /**
     * Pause or resume the game.
     * While paused, no game logic is processed except pause toggle.
     */
    PAUSE,

    /**
     * Quit the game immediately.
     * Signals to stop the game loop and return to menu.
     */
    QUIT,

    /**
     * No action (used for game ticks without player input).
     * Game still applies gravity and other automatic effects.
     */
    NONE
}

