package model;

/**
 * Immutable representation of the game state.
 * Contains the board, current falling piece, and game statistics.
 */
public class GameState {
    private final Board board;
    private final Tetromino currentPiece;
    private final int score;
    private final int linesCleared;
    private final boolean gameOver;
    private final long tickCount;
    private final GameDifficulty difficulty;

    /**
     * Constructs a new GameState with default settings (NORMAL difficulty).
     */
    public GameState() {
        this.board = new Board();
        this.currentPiece = new Tetromino(new Point(0, 0));
        this.score = 0;
        this.linesCleared = 0;
        this.gameOver = false;
        this.tickCount = 0;
        this.difficulty = GameDifficulty.NORMAL;
    }

    /**
     * Constructs a new GameState with specified difficulty level.
     *
     * @param difficulty the game difficulty level
     */
    public GameState(GameDifficulty difficulty) {
        this.board = new Board();
        this.currentPiece = new Tetromino(new Point(0, 0));
        this.score = 0;
        this.linesCleared = 0;
        this.gameOver = false;
        this.tickCount = 0;
        this.difficulty = difficulty;
    }

    private GameState(Board board, Tetromino currentPiece, int score, 
                     int linesCleared, boolean gameOver, long tickCount, GameDifficulty difficulty) {
        this.board = board;
        this.currentPiece = currentPiece;
        this.score = score;
        this.linesCleared = linesCleared;
        this.gameOver = gameOver;
        this.tickCount = tickCount;
        this.difficulty = difficulty;
    }

    // Builder pattern for creating modified game states
    public static class Builder {
        private Board board;
        private Tetromino currentPiece;
        private int score;
        private int linesCleared;
        private boolean gameOver;
        private long tickCount;
        private GameDifficulty difficulty;

        /**
         * Constructs a Builder initialized with values from the given GameState.
         *
         * @param state the GameState to copy values from
         */
        public Builder(GameState state) {
            this.board = state.board;
            this.currentPiece = state.currentPiece;
            this.score = state.score;
            this.linesCleared = state.linesCleared;
            this.gameOver = state.gameOver;
            this.tickCount = state.tickCount;
            this.difficulty = state.difficulty;
        }

        public Builder withBoard(Board board) {
            this.board = board;
            return this;
        }

        public Builder withCurrentPiece(Tetromino piece) {
            this.currentPiece = piece;
            return this;
        }

        public Builder withScore(int score) {
            this.score = score;
            return this;
        }

        public Builder withLinesCleared(int linesCleared) {
            this.linesCleared = linesCleared;
            return this;
        }

        public Builder withGameOver(boolean gameOver) {
            this.gameOver = gameOver;
            return this;
        }

        public Builder withTickCount(long tickCount) {
            this.tickCount = tickCount;
            return this;
        }

        public Builder withDifficulty(GameDifficulty difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        /**
         * Builds and returns a new GameState with the configured values.
         *
         * @return a new GameState instance
         */
        public GameState build() {
            return new GameState(board, currentPiece, score, linesCleared, gameOver, tickCount, difficulty);
        }
    }

    public Board getBoard() {
        return board;
    }

    public Tetromino getCurrentPiece() {
        return currentPiece;
    }

    public int getScore() {
        return score;
    }

    public int getLinesCleared() {
        return linesCleared;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public long getTickCount() {
        return tickCount;
    }

    /**
     * Returns the current game difficulty level.
     *
     * @return the GameDifficulty
     */
    public GameDifficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public String toString() {
        return String.format("GameState{score=%d, lines=%d, gameOver=%b, tick=%d}",
                score, linesCleared, gameOver, tickCount);
    }
}
