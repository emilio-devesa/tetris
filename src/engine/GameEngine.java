package engine;

import model.*;
import java.util.Set;

/**
 * Core game engine implementing the Tetris game logic.
 * Handles movement, collision detection, gravity, wall kick, and line clearing.
 * 
 * The engine is stateless - it takes a GameState and produces a new one.
 * All decisions are pure functions of the input state and action.
 *
 * Features:
 * - Gravity-based falling with difficulty scaling
 * - Wall kick for rotation (automatic horizontal adjustment near walls)
 * - Collision detection with board boundaries and fixed pieces
 * - Line completion detection and clearing with score calculation
 * - Game over condition when new piece cannot spawn
 */
public class GameEngine {
    private static final int SCORE_PER_LINE = 100;
    private static final int WALL_KICK_OFFSET = 1; // Try one cell away on wall kick

    /**
     * Processes a game tick with an optional action.
     * 
     * This method:
     * 1. Processes any player input action
     * 2. Applies gravity at intervals based on difficulty
     * 3. Increments the tick counter
     * 
     * If the game is paused, only pause toggle is processed.
     * 
     * @param state  current game state
     * @param action player action (or NONE for no input)
     * @return new game state after processing
     */
    public GameState tick(GameState state, GameAction action) {
        if (state.isGameOver()) {
            return state;
        }

        GameState updated = state;

        // Handle pause toggle
        if (action == GameAction.PAUSE) {
            return new GameState.Builder(updated)
                    .withPaused(!updated.isPaused())
                    .build();
        }

        // If paused, don't process other actions or gravity
        if (updated.isPaused()) {
            return updated;
        }

        // Process player action
        if (action != GameAction.NONE) {
            updated = processAction(updated, action);
        }

        // Apply gravity based on difficulty
        int gravityTicks = updated.getDifficulty().getGravityTicks();
        if (updated.getTickCount() % gravityTicks == 0) {
            updated = applyGravity(updated);
        }

        // Increment tick counter
        updated = new GameState.Builder(updated)
                .withTickCount(updated.getTickCount() + 1)
                .build();

        return updated;
    }

    /**
     * Processes a player action on the current piece.
     * Attempts movement, rotation, or instant drop.
     *
     * @param state  current game state
     * @param action the action to perform
     * @return new game state after action processing
     */
    private GameState processAction(GameState state, GameAction action) {
        Tetromino current = state.getCurrentPiece();
        Board board = state.getBoard();
        Tetromino moved;

        switch (action) {
            case LEFT:
                moved = current.moveTo(current.getPosition().translate(0, -1));
                if (canPlace(moved, board)) {
                    return new GameState.Builder(state)
                            .withCurrentPiece(moved)
                            .build();
                }
                break;

            case RIGHT:
                moved = current.moveTo(current.getPosition().translate(0, 1));
                if (canPlace(moved, board)) {
                    return new GameState.Builder(state)
                            .withCurrentPiece(moved)
                            .build();
                }
                break;

            case DOWN:
                moved = current.moveTo(current.getPosition().translate(1, 0));
                if (canPlace(moved, board)) {
                    return new GameState.Builder(state)
                            .withCurrentPiece(moved)
                            .build();
                } else {
                    // Can't move down - lock piece and spawn new one
                    return lockPieceAndSpawnNew(state);
                }

            case ROTATE:
                Tetromino rotated = current.rotateClockwise();
                if (canPlace(rotated, board)) {
                    return new GameState.Builder(state)
                            .withCurrentPiece(rotated)
                            .build();
                } else if (attemptWallKick(rotated, board)) {
                    // Try wall kick: shift left or right to fit rotation
                    Tetromino wallKicked = rotated.moveTo(
                            rotated.getPosition().translate(0, getWallKickDirection(rotated, board))
                    );
                    if (canPlace(wallKicked, board)) {
                        return new GameState.Builder(state)
                                .withCurrentPiece(wallKicked)
                                .build();
                    }
                }
                // Rotation blocked even with wall kick - do nothing
                break;

            case DROP:
                return dropToBottom(state);

            case NONE:
            default:
                // No action
                break;
        }

        return state;
    }

    /**
     * Applies gravity to the falling piece.
     * If piece can't fall, it locks in place and a new piece spawns.
     *
     * @param state current game state
     * @return new game state after gravity application
     */
    private GameState applyGravity(GameState state) {
        Tetromino current = state.getCurrentPiece();
        Tetromino fallen = current.moveTo(current.getPosition().translate(1, 0));

        if (canPlace(fallen, state.getBoard())) {
            return new GameState.Builder(state)
                    .withCurrentPiece(fallen)
                    .build();
        }

        // Piece has landed
        return lockPieceAndSpawnNew(state);
    }

    /**
     * Instantly drops the piece to the bottom.
     * Locks piece and spawns new one immediately.
     *
     * @param state current game state
     * @return new game state after drop
     */
    private GameState dropToBottom(GameState state) {
        Tetromino current = state.getCurrentPiece();
        Tetromino dropped = current;

        while (true) {
            Tetromino next = dropped.moveTo(dropped.getPosition().translate(1, 0));
            if (canPlace(next, state.getBoard())) {
                dropped = next;
            } else {
                break;
            }
        }

        GameState stateAfterDrop = new GameState.Builder(state)
                .withCurrentPiece(dropped)
                .build();

        return lockPieceAndSpawnNew(stateAfterDrop);
    }

    /**
     * Locks the current piece on the board and spawns a new one.
     * Detects and clears completed rows, updates score and statistics.
     *
     * @param state current game state before locking
     * @return new game state with piece locked and new piece spawned
     */
    private GameState lockPieceAndSpawnNew(GameState state) {
        Tetromino current = state.getCurrentPiece();
        Board boardWithPiece = state.getBoard().addPiece(current.getOccupiedCells());

        // Check for complete rows
        Set<Integer> completeRows = boardWithPiece.getCompleteRows();
        Board boardAfterClear = boardWithPiece.clearRows(completeRows);

        // Calculate score with difficulty multiplier
        int linesCleared = completeRows.size();
        int scoreGained = (int)(linesCleared * SCORE_PER_LINE * state.getDifficulty().getScoreMultiplier());
        int newScore = state.getScore() + scoreGained;
        int newLinesCleared = state.getLinesCleared() + linesCleared;

        // Spawn new piece at top-left
        Tetromino newPiece = Tetromino.createRandom(new Point(0, 0));

        // Check if new piece can be placed (game over condition)
        if (!canPlace(newPiece, boardAfterClear)) {
            return new GameState.Builder(state)
                    .withBoard(boardAfterClear)
                    .withCurrentPiece(newPiece)
                    .withScore(newScore)
                    .withLinesCleared(newLinesCleared)
                    .withGameOver(true)
                    .build();
        }

        return new GameState.Builder(state)
                .withBoard(boardAfterClear)
                .withCurrentPiece(newPiece)
                .withScore(newScore)
                .withLinesCleared(newLinesCleared)
                .build();
    }

    /**
     * Checks if a piece can be placed at its current position.
     * Validates that all occupied cells are within bounds and not occupied.
     *
     * @param piece the tetromino to check
     * @param board the current game board
     * @return true if piece can be placed, false otherwise
     */
    private boolean canPlace(Tetromino piece, Board board) {
        Set<Point> cells = piece.getOccupiedCells();
        return board.canPlacePiece(cells);
    }

    /**
     * Determines if a rotated piece near a wall can be wall-kicked.
     * Wall kick allows rotation to succeed by shifting the piece one cell sideways.
     *
     * @param rotatedPiece the rotated tetromino
     * @param board        the current board
     * @return true if wall kick might help
     */
    private boolean attemptWallKick(Tetromino rotatedPiece, Board board) {
        // Check if rotation is blocked
        return !canPlace(rotatedPiece, board);
    }

    /**
     * Determines the direction to shift for wall kick (left or right).
     * Attempts right first, then left.
     *
     * @param rotatedPiece the rotated tetromino
     * @param board        the current board
     * @return -1 for left, +1 for right, 0 if no wall kick possible
     */
    private int getWallKickDirection(Tetromino rotatedPiece, Board board) {
        // Try right first
        Tetromino rightShift = rotatedPiece.moveTo(
                rotatedPiece.getPosition().translate(0, WALL_KICK_OFFSET)
        );
        if (canPlace(rightShift, board)) {
            return WALL_KICK_OFFSET;
        }

        // Try left
        Tetromino leftShift = rotatedPiece.moveTo(
                rotatedPiece.getPosition().translate(0, -WALL_KICK_OFFSET)
        );
        if (canPlace(leftShift, board)) {
            return -WALL_KICK_OFFSET;
        }

        return 0;
    }

    /**
     * Returns the score per line cleared (base value before difficulty multiplier).
     *
     * @return base score for one line
     */
    public static int getScorePerLine() {
        return SCORE_PER_LINE;
    }
}
