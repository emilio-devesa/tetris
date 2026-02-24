package model;

/**
 * S-shaped tetromino piece.
 */
public class STetromino extends Tetromino {
    private static final int[][] ROTATION_0 = {
        {0, 1}, {0, 2}, {1, 0}, {1, 1}
    };
    private static final int[][] ROTATION_1 = {
        {0, 0}, {1, 0}, {1, 1}, {2, 1}
    };
    private static final int[][] ROTATION_2 = {
        {1, 1}, {1, 2}, {2, 0}, {2, 1}
    };
    private static final int[][] ROTATION_3 = {
        {0, 1}, {1, 1}, {1, 2}, {2, 2}
    };

    private static final int[][][] ROTATIONS = {
        ROTATION_0, ROTATION_1, ROTATION_2, ROTATION_3
    };

    /**
     * Constructs an S-tetromino at the given position with specified rotation.
     *
     * @param position       the top-left corner of the bounding box
     * @param rotationState  the rotation state (0-3)
     */
    public STetromino(Point position, int rotationState) {
        super(position, rotationState, Type.S);
    }

    /**
     * Constructs an S-tetromino at the given position with rotation 0.
     *
     * @param position the top-left corner of the bounding box
     */
    public STetromino(Point position) {
        this(position, 0);
    }

    @Override
    protected int[][][] getRotations() {
        return ROTATIONS;
    }

    @Override
    public Tetromino moveTo(Point newPosition) {
        return new STetromino(newPosition, rotationState);
    }

    @Override
    public Tetromino rotateClockwise() {
        return new STetromino(position, (rotationState + 1) % 4);
    }
}