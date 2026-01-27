package engine;

import model.*;
import java.util.Set;

/**
 * Core game engine implementing the Tetris game logic.
 * Handles movement, collision detection, gravity, and line clearing.
 * 
 * This class manages the game loop with tick-based updates.
 * The engine is stateless - it takes a GameState and produces a new one.
 */
public class GameEngine {
    private static final int GRAVITY_TICKS = 10; // Fall every N ticks
    private static final int SCORE_PER_LINE = 100;

    /**
     * Processes a game tick with an optional action.
     * 
     * @param state Current game state
     * @param action Player action (or NONE for no input)
     * @return New game state after processing
     */
    public GameState tick(GameState state, GameAction action) {
        if (state.isGameOver()) {
            return state;
        }

        GameState updated = state;

        // Process player action
        if (action != GameAction.NONE) {
            updated = processAction(updated, action);
        }

        // Apply gravity
        if (updated.getTickCount() % GRAVITY_TICKS == 0) {
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
                }
                // Rotation blocked - do nothing
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
     * If piece can't fall, it locks in place.
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
     */
    private GameState lockPieceAndSpawnNew(GameState state) {
        Tetromino current = state.getCurrentPiece();
        Board boardWithPiece = state.getBoard().addPiece(current.getOccupiedCells());

        // Check for complete rows
        Set<Integer> completeRows = boardWithPiece.getCompleteRows();
        Board boardAfterClear = boardWithPiece.clearRows(completeRows);

        int newScore = state.getScore() + (completeRows.size() * SCORE_PER_LINE);
        int newLinesCleared = state.getLinesCleared() + completeRows.size();

        // Spawn new piece at top-left
        Tetromino newPiece = new Tetromino(new Point(0, 0));

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
     */
    private boolean canPlace(Tetromino piece, Board board) {
        Set<Point> cells = piece.getOccupiedCells();
        return board.canPlacePiece(cells);
    }

    public static int getGravityTicks() {
        return GRAVITY_TICKS;
    }

    public static int getScorePerLine() {
        return SCORE_PER_LINE;
    }
}
