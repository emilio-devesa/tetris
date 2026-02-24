package model;

/**
 * O-shaped tetromino piece.
 */
public class OTetromino extends Tetromino {
    private static final int[][] ROTATION = {
        {1, 1}, {1, 2}, {2, 1}, {2, 2}
    };

    private static final int[][][] ROTATIONS = {
        ROTATION, ROTATION, ROTATION, ROTATION
    };

    /**
     * Constructs an O-tetromino at the given position with specified rotation.
     *
     * @param position       the top-left corner of the bounding box
     * @param rotationState  the rotation state (0-3)
     */
    public OTetromino(Point position, int rotationState) {
        super(position, rotationState, Type.O);
    }

    /**
     * Constructs an O-tetromino at the given position with rotation 0.
     *
     * @param position the top-left corner of the bounding box
     */
    public OTetromino(Point position) {
        this(position, 0);
    }

    @Override
    protected int[][][] getRotations() {
        return ROTATIONS;
    }

    @Override
    public Tetromino moveTo(Point newPosition) {
        return new OTetromino(newPosition, rotationState);
    }

    @Override
    public Tetromino rotateClockwise() {
        return new OTetromino(position, (rotationState + 1) % 4);
    }
}