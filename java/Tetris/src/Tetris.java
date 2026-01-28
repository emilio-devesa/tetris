import controller.GameController;
import view.GameView;
import view.Renderer;
import view.SwingRenderer;

/**
 * Main entry point for the Tetris game.
 * Supports both terminal and GUI (Swing) modes.
 *
 * Usage:
 *   java Tetris           - Terminal mode (default)
 *   java Tetris --gui     - Swing GUI mode
 */
public class Tetris {
    public static void main(String[] args) {
        // Determine which view to use
        GameView view;
        if (args.length > 0 && args[0].equals("--gui")) {
            view = new SwingRenderer();
        } else {
            view = new Renderer();
        }

        GameController controller = new GameController(view);
        controller.play();
    }
}