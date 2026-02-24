package model;

/**
 * Z-shaped tetromino piece.
 */
public class ZTetromino extends Tetromino {
    private static final int[][] ROTATION_0 = {
        {0, 0}, {0, 1}, {1, 1}, {1, 2}
    };
    private static final int[][] ROTATION_1 = {
        {0, 1}, {1, 0}, {1, 1}, {2, 0}
    };
    private static final int[][] ROTATION_2 = {
        {1, 0}, {1, 1}, {2, 1}, {2, 2}
    };
    private static final int[][] ROTATION_3 = {
        {0, 2}, {1, 1}, {1, 2}, {2, 1}
    };

    private static final int[][][] ROTATIONS = {
        ROTATION_0, ROTATION_1, ROTATION_2, ROTATION_3
    };

    /**
     * Constructs a Z-tetromino at the given position with specified rotation.
     *
     * @param position       the top-left corner of the bounding box
     * @param rotationState  the rotation state (0-3)
     */
    public ZTetromino(Point position, int rotationState) {
        super(position, rotationState, Type.Z);
    }

    /**
     * Constructs a Z-tetromino at the given position with rotation 0.
     *
     * @param position the top-left corner of the bounding box
     */
    public ZTetromino(Point position) {
        this(position, 0);
    }

    @Override
    protected int[][][] getRotations() {
        return ROTATIONS;
    }

    @Override
    public Tetromino moveTo(Point newPosition) {
        return new ZTetromino(newPosition, rotationState);
    }

    @Override
    public Tetromino rotateClockwise() {
        return new ZTetromino(position, (rotationState + 1) % 4);
    }
}