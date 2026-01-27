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

    public GameState() {
        this.board = new Board();
        this.currentPiece = new Tetromino(new Point(0, 0));
        this.score = 0;
        this.linesCleared = 0;
        this.gameOver = false;
        this.tickCount = 0;
    }

    private GameState(Board board, Tetromino currentPiece, int score, 
                     int linesCleared, boolean gameOver, long tickCount) {
        this.board = board;
        this.currentPiece = currentPiece;
        this.score = score;
        this.linesCleared = linesCleared;
        this.gameOver = gameOver;
        this.tickCount = tickCount;
    }

    // Builder pattern for creating modified game states
    public static class Builder {
        private Board board;
        private Tetromino currentPiece;
        private int score;
        private int linesCleared;
        private boolean gameOver;
        private long tickCount;

        public Builder(GameState state) {
            this.board = state.board;
            this.currentPiece = state.currentPiece;
            this.score = state.score;
            this.linesCleared = state.linesCleared;
            this.gameOver = state.gameOver;
            this.tickCount = state.tickCount;
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

        public GameState build() {
            return new GameState(board, currentPiece, score, linesCleared, gameOver, tickCount);
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

    @Override
    public String toString() {
        return String.format("GameState{score=%d, lines=%d, gameOver=%b, tick=%d}",
                score, linesCleared, gameOver, tickCount);
    }
}
