package model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract base class for all tetromino pieces in the Tetris game.
 * Each tetromino has 4 rotation states and fits within a 4x4 bounding box.
 * Supports all 7 standard Tetris pieces: I, O, T, S, Z, J, L.
 *
 * @author Tetris Engine
 */
public abstract class Tetromino {
    public enum Type {
        I, O, T, S, Z, J, L
    }

    protected final Point position;  // Top-left corner of bounding box
    protected final int rotationState; // 0-3
    protected final Type type;

    /**
     * Constructs a tetromino at the given position with specified rotation.
     *
     * @param position       the top-left corner of the bounding box
     * @param rotationState  the rotation state (0-3, automatically normalized to 0-3)
     * @param type           the type of tetromino
     */
    protected Tetromino(Point position, int rotationState, Type type) {
        this.position = position;
        this.rotationState = rotationState % 4;
        this.type = type;
    }

    /**
     * Returns the rotation patterns for this tetromino.
     * Each rotation is an array of [row, col] offsets relative to the bounding box.
     *
     * @return 4x4x2 array of rotations
     */
    protected abstract int[][][] getRotations();

    /**
     * Returns the absolute coordinates of all blocks that form this tetromino.
     * Each block's position is calculated by adding the bounding box offset
     * to the tetromino's position.
     *
     * @return a set of Points representing occupied cells
     */
    public Set<Point> getOccupiedCells() {
        Set<Point> cells = new HashSet<>();
        int[][] pattern = getRotations()[rotationState];

        for (int[] offset : pattern) {
            int row = position.getRow() + offset[0];
            int col = position.getCol() + offset[1];
            cells.add(new Point(row, col));
        }

        return cells;
    }

    /**
     * Creates a new tetromino at the specified position.
     * Maintains the current rotation state.
     * The original tetromino is unchanged (immutability).
     *
     * @param newPosition the new position
     * @return a new Tetromino at the specified position
     */
    public abstract Tetromino moveTo(Point newPosition);

    /**
     * Creates a new tetromino rotated clockwise.
     * Advances the rotation state by one (0→1→2→3→0).
     * The original tetromino is unchanged (immutability).
     *
     * @return a new Tetromino rotated clockwise
     */
    public abstract Tetromino rotateClockwise();

    /**
     * Creates a random tetromino at the given position.
     *
     * @param position the top-left corner of the bounding box
     * @return a new random Tetromino
     */
    public static Tetromino createRandom(Point position) {
        Type[] types = Type.values();
        Type randomType = types[(int)(Math.random() * types.length)];
        return create(randomType, position);
    }

    /**
     * Creates a tetromino of the specified type at the given position.
     *
     * @param type     the type of tetromino
     * @param position the top-left corner of the bounding box
     * @return a new Tetromino of the specified type
     */
    public static Tetromino create(Type type, Point position) {
        switch (type) {
            case I: return new ITetromino(position);
            case O: return new OTetromino(position);
            case T: return new TTetromino(position);
            case S: return new STetromino(position);
            case Z: return new ZTetromino(position);
            case J: return new JTetromino(position);
            case L: return new LTetromino(position);
            default: throw new IllegalArgumentException("Unknown tetromino type: " + type);
        }
    }

    /**
     * Returns the type of this tetromino.
     *
     * @return the Type enum value
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the position (top-left corner of bounding box) of this tetromino.
     *
     * @return the position Point
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Returns the current rotation state (0-3).
     *
     * @return rotation state
     */
    public int getRotationState() {
        return rotationState;
    }

    /**
     * Returns a string representation of this tetromino.
     *
     * @return string with type, position and rotation information
     */
    @Override
    public String toString() {
        return String.format("Tetromino(type=%s, pos=%s, rotation=%d)", type, position, rotationState);
    }
}
