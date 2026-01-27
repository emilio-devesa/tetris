import controller.GameController;

/**
 * Main entry point for the Tetris game.
 * Initializes and starts the game.
 */
public class Tetris {
    public static void main(String[] args) {
        GameController controller = new GameController();
        controller.play();
    }
}