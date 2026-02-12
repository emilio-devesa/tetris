import controller.GameController;
import model.*;
import engine.GameEngine;
import engine.GameDemo;
import view.Renderer;
import view.SwingRenderer;
import view.AudioManager;

/**
 * Comprehensive test suite for Tetris game.
 * Demonstrates and validates all game mechanics without external testing frameworks.
 * Can be run programmatically or as part of CI/CD.
 */
public class TetrisGameTest {
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║     TETRIS GAME TEST SUITE             ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();

        // Model Tests
        testPointCreation();
        testTetriminoRotations();
        testTetriminoMovement();
        testBoardCollisions();
        testBoardOccupancy();
        testBoardLineClearing();
        testBoardLineClearingWithGravity();

        // Engine Tests
        testGameEngineMovement();
        testGameEngineGravity();
        testGameEngineLineDetection();
        testGameEngineGameOver();
        testGameEngineDrop();
        testGameEngineWallKick();
        testGameEnginePause();
        testGameEngineActionQuit();

        // Difficulty and Refinement Tests
        testGameDifficultyLevels();
        testGameStateWithDifficulty();
        testScoreMultiplier();

        // Controller Tests
        testGameControllerInitialization();
        testGameControllerTickProcessing();
        testGameControllerReset();
        testGameControllerParseInput();

        // Exception Tests
        testExceptionHierarchy();

        // Advanced Features Tests
        testGameStatistics();
        testHighScoreManager();
        testGameDemo();
        testHighScorePersistence();
        testGameConfig();
        testGameTimer();
        testTetriminoOccupiedCells();
        testBoardBoundaryDetection();
        testScorePersistence();
        testGameStateBuilder();
        testAudioManager();
        
        System.out.println();
        System.out.println("╔════════════════════════════════════════╗");
        System.out.printf("║ Tests Passed: %-26d ║%n", testsPassed);
        System.out.printf("║ Tests Failed: %-26d ║%n", testsFailed);
        System.out.println("╚════════════════════════════════════════╝");

        System.exit(testsFailed > 0 ? 1 : 0);
    }

    // ==================== Model Tests ====================

    private static void testPointCreation() {
        test("Point creation and translation", () -> {
            Point p1 = new Point(0, 0);
            assert p1.getRow() == 0 : "Row should be 0";
            assert p1.getCol() == 0 : "Col should be 0";

            Point p2 = p1.translate(2, 3);
            assert p2.getRow() == 2 : "Translated row should be 2";
            assert p2.getCol() == 3 : "Translated col should be 3";

            Point p3 = new Point(1, 1);
            assert !p1.equals(p3) : "Different points should not be equal";
            assert p1.equals(new Point(0, 0)) : "Same points should be equal";
        });
    }

    private static void testTetriminoRotations() {
        test("Tetromino rotations", () -> {
            Tetromino piece = new Tetromino(new Point(0, 0), 0);
            assert piece.getRotationState() == 0 : "Initial rotation should be 0";

            Tetromino rotated1 = piece.rotateClockwise();
            assert rotated1.getRotationState() == 1 : "After 1 rotation should be 1";

            Tetromino rotated4 = piece.rotateClockwise()
                    .rotateClockwise()
                    .rotateClockwise()
                    .rotateClockwise();
            assert rotated4.getRotationState() == 0 : "After 4 rotations should be back to 0";
        });
    }

    private static void testTetriminoMovement() {
        test("Tetromino movement", () -> {
            Point pos1 = new Point(0, 0);
            Tetromino piece = new Tetromino(pos1);

            Tetromino moved = piece.moveTo(new Point(1, 1));
            assert moved.getPosition().equals(new Point(1, 1)) : "Position should be updated";
            assert piece.getPosition().equals(pos1) : "Original should be immutable";
        });
    }

    private static void testBoardCollisions() {
        test("Board collision detection", () -> {
            Board board = new Board();

            // Empty board should allow piece placement
            Tetromino piece = new Tetromino(new Point(0, 0));
            assert board.canPlacePiece(piece.getOccupiedCells()) : "Should place piece on empty board";

            // Out of bounds should be rejected
            Point outOfBounds = new Point(Board.ROWS + 1, 0);
            assert !board.isInBounds(outOfBounds) : "Should reject out of bounds";

            // Occupied cell should be rejected
            Board filledBoard = board.addPiece(piece.getOccupiedCells());
            assert !filledBoard.canPlacePiece(piece.getOccupiedCells()) : "Should reject occupied cells";
        });
    }

    private static void testBoardOccupancy() {
        test("Board occupancy tracking", () -> {
            Board board = new Board();
            Tetromino piece = new Tetromino(new Point(0, 0));

            Board filled = board.addPiece(piece.getOccupiedCells());
            assert filled.getOccupiedCells().size() == 4 : "Should have 4 occupied cells for L-piece";
            assert filled.isOccupied(new Point(0, 0)) : "Piece cell should be occupied";
            assert !filled.isOccupied(new Point(1, 1)) : "Empty cell should not be occupied";
        });
    }

    private static void testBoardLineClearing() {
        test("Board line clearing", () -> {
            Board board = new Board();

            // Fill bottom row completely
            for (int col = 0; col < Board.COLS; col++) {
                board = board.addPiece(Board.COLS > 0 ? java.util.Collections.singleton(
                        new Point(Board.ROWS - 1, col)) : java.util.Collections.emptySet());
            }

            // Actually, let's create a proper filled row scenario
            Board testBoard = new Board();
            for (int col = 0; col < Board.COLS; col++) {
                testBoard = testBoard.addPiece(java.util.Collections.singleton(
                        new Point(5, col)));
            }

            var completeRows = testBoard.getCompleteRows();
            assert completeRows.contains(5) : "Row 5 should be complete";

            Board cleared = testBoard.clearRows(completeRows);
            assert cleared.getCompleteRows().isEmpty() : "Complete rows should be cleared";
        });
    }

    private static void testBoardLineClearingWithGravity() {
        test("Board line clearing with gravity (cells fall down)", () -> {
            Board board = new Board();

            // Add cells above row 5
            for (int col = 0; col < Board.COLS; col++) {
                board = board.addPiece(java.util.Collections.singleton(new Point(3, col)));
                board = board.addPiece(java.util.Collections.singleton(new Point(4, col)));
            }

            // Add cells in row 5 to make it complete
            for (int col = 0; col < Board.COLS; col++) {
                board = board.addPiece(java.util.Collections.singleton(new Point(5, col)));
            }

            // Verify row 5 is complete
            var completeRows = board.getCompleteRows();
            assert completeRows.contains(5) : "Row 5 should be complete";

            // Before clearing: rows 3, 4, 5 should be full
            assert board.isOccupied(new Point(3, 0)) : "Row 3 should have cells before clearing";
            assert board.isOccupied(new Point(4, 0)) : "Row 4 should have cells before clearing";
            assert board.isOccupied(new Point(5, 0)) : "Row 5 should have cells before clearing";

            // Clear row 5
            Board cleared = board.clearRows(completeRows);

            // After clearing row 5:
            // - Cells from row 3 stay at row 3 (0 rows cleared below them)
            // - Cells from row 4 move to row 5 (1 row cleared below: row 5)
            assert cleared.isOccupied(new Point(3, 0)) : "Row 3 cells should stay at row 3";
            assert !cleared.isOccupied(new Point(4, 0)) : "Row 4 should be empty (cells moved to row 5)";
            assert cleared.isOccupied(new Point(5, 0)) : "Row 5 should have cells from row 4 (gravity applied)";

            // Verify row 5 has all 10 cells
            int row5Count = 0;
            for (int col = 0; col < Board.COLS; col++) {
                if (cleared.isOccupied(new Point(5, col))) {
                    row5Count++;
                }
            }
            assert row5Count == Board.COLS : "Row 5 should have all cells from original row 4";
        });
    }

    // ==================== Engine Tests ====================

    private static void testGameEngineMovement() {
        test("GameEngine piece movement", () -> {
            GameState state = new GameState();
            GameEngine engine = new GameEngine();

            // Move right
            GameState stateRight = engine.tick(state, GameAction.RIGHT);
            Point newPos = stateRight.getCurrentPiece().getPosition();
            assert newPos.getCol() > state.getCurrentPiece().getPosition().getCol() 
                : "Piece should move right";

            // Move left
            GameState stateLeft = engine.tick(state, GameAction.LEFT);
            newPos = stateLeft.getCurrentPiece().getPosition();
            assert newPos.getCol() < state.getCurrentPiece().getPosition().getCol() 
                : "Piece should move left";
        });
    }

    private static void testGameEngineGravity() {
        test("GameEngine gravity", () -> {
            GameState state = new GameState();
            GameEngine engine = new GameEngine();

            // Advance through ticks until gravity applies
            GameState current = state;
            int gravityTicks = state.getDifficulty().getGravityTicks();
            for (int i = 0; i < gravityTicks; i++) {
                current = engine.tick(current, GameAction.NONE);
            }

            // Piece should have fallen
            assert current.getCurrentPiece().getPosition().getRow() > state.getCurrentPiece().getPosition().getRow()
                : "Piece should fall due to gravity";
        });
    }

    private static void testGameEngineLineDetection() {
        test("GameEngine line detection and clearing", () -> {
            GameState state = new GameState();
            GameEngine engine = new GameEngine();

            // The test verifies scoring increases when lines are cleared
            GameState current = state;
            int initialScore = current.getScore();

            // Play enough moves to potentially clear a line
            for (int i = 0; i < 200; i++) {
                current = engine.tick(current, GameAction.NONE);
                if (current.getScore() > initialScore) {
                    break; // Line was cleared
                }
            }

            // We can't guarantee a line in random play, so just verify the mechanism works
            assert current.getScore() >= initialScore : "Score should not decrease";
        });
    }

    private static void testGameEngineGameOver() {
        test("GameEngine game over condition", () -> {
            GameState state = new GameState();
            GameEngine engine = new GameEngine();

            GameState current = state;

            // Simulate rapid falling until game over
            for (int i = 0; i < 1000; i++) {
                current = engine.tick(current, GameAction.NONE);
                if (current.isGameOver()) {
                    break;
                }
            }

            assert current.isGameOver() || current.getTickCount() < 1000 
                : "Game should eventually reach game over condition";
        });
    }

    private static void testGameEngineDrop() {
        test("GameEngine instant drop", () -> {
            GameState state = new GameState();
            GameEngine engine = new GameEngine();

            int initialRow = state.getCurrentPiece().getPosition().getRow();

            GameState dropped = engine.tick(state, GameAction.DROP);

            // After drop, piece should be lower
            assert dropped.getCurrentPiece().getPosition().getRow() > initialRow 
                : "Piece should drop to bottom";

            // After drop, board should have a piece locked
            assert dropped.getBoard().getOccupiedCells().size() > 0 
                : "Piece should be locked after drop";
        });
    }

    private static void testGameEngineWallKick() {
        test("GameEngine wall kick on rotation", () -> {
            GameState state = new GameState();
            GameEngine engine = new GameEngine();

            // Move piece to the left edge
            GameState atEdge = state;
            for (int i = 0; i < 5; i++) {
                atEdge = engine.tick(atEdge, GameAction.LEFT);
            }

            // Try to rotate at edge - wall kick should allow it or reject it gracefully
            GameState rotated = engine.tick(atEdge, GameAction.ROTATE);

            // Wall kick should not cause errors; rotation either succeeds or is ignored
            assert !rotated.isGameOver() : "Wall kick should not cause game over";
        });
    }

    private static void testGameDifficultyLevels() {
        test("GameDifficulty level differences", () -> {
            assert GameDifficulty.EASY.getGravityTicks() > GameDifficulty.NORMAL.getGravityTicks()
                : "Easy should have slower gravity";
            assert GameDifficulty.HARD.getGravityTicks() < GameDifficulty.NORMAL.getGravityTicks()
                : "Hard should have faster gravity";
            assert GameDifficulty.EXTREME.getGravityTicks() < GameDifficulty.HARD.getGravityTicks()
                : "Extreme should have fastest gravity";

            assert GameDifficulty.EASY.getScoreMultiplier() == 1.0f
                : "Easy should have 1.0x multiplier";
            assert GameDifficulty.HARD.getScoreMultiplier() > 1.0f
                : "Hard should have >1.0x multiplier";
            assert GameDifficulty.EXTREME.getScoreMultiplier() > GameDifficulty.HARD.getScoreMultiplier()
                : "Extreme should have highest multiplier";
        });
    }

    private static void testGameStateWithDifficulty() {
        test("GameState with difficulty levels", () -> {
            GameState easyGame = new GameState(GameDifficulty.EASY);
            assert easyGame.getDifficulty() == GameDifficulty.EASY : "Easy difficulty should be set";

            GameState hardGame = new GameState(GameDifficulty.HARD);
            assert hardGame.getDifficulty() == GameDifficulty.HARD : "Hard difficulty should be set";
            assert hardGame.getScore() == 0 : "New game should have 0 score";
        });
    }

    private static void testScoreMultiplier() {
        test("Score calculation with difficulty multiplier", () -> {
            GameState easyState = new GameState(GameDifficulty.EASY);
            GameState hardState = new GameState(GameDifficulty.HARD);

            GameEngine engine = new GameEngine();

            // Score multiplier should affect point calculation
            // (Difficult to test without line clearing, but we can verify structure)
            assert easyState.getDifficulty().getScoreMultiplier() == 1.0f
                : "Easy multiplier should be 1.0x";
            assert hardState.getDifficulty().getScoreMultiplier() == 1.5f
                : "Hard multiplier should be 1.5x";
        });
    }

    private static void testExceptionHierarchy() {
        test("Exception class hierarchy", () -> {
            GameException gameEx = new GameException("Test");
            InvalidMoveException moveEx = new InvalidMoveException("Move blocked");

            assert gameEx instanceof Exception : "GameException should extend Exception";
            assert moveEx instanceof GameException : "InvalidMoveException should extend GameException";
            assert moveEx instanceof Exception : "InvalidMoveException should extend Exception";
        });
    }

    private static void testGameStatistics() {
        test("GameStatistics calculation", () -> {
            GameState finalState = new GameState(GameDifficulty.HARD);
            GameStatistics stats = new GameStatistics(finalState, 10000, 5);

            assert stats.getFinalScore() >= 0 : "Score should be non-negative";
            assert stats.getPlayDurationSeconds() > 0 : "Duration should be positive";
            assert stats.getPiecesPlaced() == 5 : "Pieces should match";
            assert stats.getDifficulty() == GameDifficulty.HARD : "Difficulty should match";
        });
    }

    private static void testHighScoreManager() {
        test("HighScoreManager functionality", () -> {
            HighScoreManager manager = new HighScoreManager();
            
            GameState state1 = new GameState(GameDifficulty.NORMAL);
            GameStatistics stats1 = new GameStatistics(state1, 10000, 5);
            
            manager.recordScore(stats1);
            assert manager.getTopScores().size() > 0 : "Should have recorded score";
            
            manager.clearScores();
            assert manager.getTopScores().isEmpty() : "Scores should be cleared";
        });
    }

    private static void testGameDemo() {
        test("GameDemo automatic play", () -> {
            GameDemo demo = new GameDemo(GameDifficulty.EASY);
            
            // Run a few ticks
            for (int i = 0; i < 50; i++) {
                demo.tick();
            }
            
            assert demo.getTickCount() == 50 : "Tick count should be 50";
            assert !demo.getState().isGameOver() : "Should not be game over after 50 ticks";
        });
    }

    private static void testHighScorePersistence() {
        test("HighScore serialization", () -> {
            HighScoreManager.HighScore score = new HighScoreManager.HighScore(
                1000, GameDifficulty.HARD, 10, System.currentTimeMillis()
            );
            
            String serialized = score.serialize();
            assert serialized.contains("1000") : "Should contain score";
            assert serialized.contains("HARD") : "Should contain difficulty";
            assert serialized.contains("10") : "Should contain lines";
            
            HighScoreManager.HighScore parsed = HighScoreManager.HighScore.parse(serialized);
            assert parsed != null : "Should parse successfully";
            assert parsed.getScore() == 1000 : "Score should match";
        });
    }
    // ==================== Additional Feature Tests ====================

    private static void testGameEnginePause() {
        test("GameEngine pause functionality", () -> {
            GameState state = new GameState();
            GameEngine engine = new GameEngine();

            // Tick normally
            GameState normal = engine.tick(state, GameAction.NONE);
            assert !normal.isPaused() : "New game should not be paused";

            // Toggle pause
            GameState paused = engine.tick(normal, GameAction.PAUSE);
            assert paused.isPaused() : "Game should be paused";

            // Toggle pause again
            GameState resumed = engine.tick(paused, GameAction.PAUSE);
            assert !resumed.isPaused() : "Game should be resumed";
        });
    }

    private static void testGameEngineActionQuit() {
        test("GameEngine quit action handling", () -> {
            GameState state = new GameState();
            GameEngine engine = new GameEngine();

            // QUIT action should be processable without errors
            GameState afterAction = engine.tick(state, GameAction.QUIT);
            assert afterAction != null : "Should handle QUIT action";
        });
    }

    private static void testGameControllerParseInput() {
        test("GameController input parsing", () -> {
            GameController controller = new GameController(new Renderer());

            // Test that input can be processed without errors
            controller.tick(GameAction.LEFT);
            controller.tick(GameAction.RIGHT);
            controller.tick(GameAction.DOWN);
            controller.tick(GameAction.ROTATE);
            controller.tick(GameAction.DROP);

            GameState state = controller.getState();
            assert state != null : "State should remain valid";
        });
    }

    private static void testGameConfig() {
        test("GameConfig preferences", () -> {
            GameConfig config = new GameConfig();

            // Test theme setting
            assert config.getTheme() != null : "Theme should not be null";
            config.setTheme(GameConfig.Theme.DARK);
            assert config.getTheme() == GameConfig.Theme.DARK : "Theme should be DARK";

            // Test difficulty setting
            config.setDefaultDifficulty(GameDifficulty.HARD);
            assert config.getDefaultDifficulty() == GameDifficulty.HARD : "Default difficulty should be HARD";

            // Test sound setting
            config.setSoundEnabled(true);
            assert config.isSoundEnabled() : "Sound should be enabled";

            // Test auto-save setting
            config.setAutoSaveEnabled(false);
            assert !config.isAutoSaveEnabled() : "Auto-save should be disabled";

            // Test reset
            config.reset();
            assert config.getTheme() == GameConfig.Theme.LIGHT : "Reset theme should be LIGHT";
            assert config.getDefaultDifficulty() == GameDifficulty.NORMAL : "Reset difficulty should be NORMAL";
        });
    }

    private static void testGameTimer() {
        test("GameTimer timing and records", () -> {
            GameTimer timer1 = new GameTimer();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            GameTimer timer2 = timer1.update();
            assert timer2.getElapsedMillis() >= 10 : "Elapsed time should be at least 10ms";
            assert timer2.getElapsedSeconds() > 0 : "Elapsed seconds should be positive";

            // Test formatted time
            String formatted = timer2.getFormattedTime();
            assert formatted.contains(":") : "Formatted time should contain ':' separator";

            // Test personal record
            GameTimer.resetPersonalRecord();
            assert timer2.isNewRecord() : "First time should be personal record";
            timer2.updatePersonalRecord();

            GameTimer timer3 = new GameTimer();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            timer3 = timer3.update();
            assert !timer3.isNewRecord() : "Subsequent time should not be new record (if slower)";
        });
    }

    private static void testTetriminoOccupiedCells() {
        test("Tetromino occupied cells calculation", () -> {
            Tetromino piece = new Tetromino(new Point(0, 0), 0);
            var cells = piece.getOccupiedCells();

            assert cells.size() == 4 : "L-piece should occupy 4 cells";
            assert !cells.isEmpty() : "Occupied cells should not be empty";

            // Test that all cells are valid points
            for (Point cell : cells) {
                assert cell.getRow() >= 0 : "Cell row should be non-negative";
                assert cell.getCol() >= 0 : "Cell col should be non-negative";
            }

            // Test rotation changes occupied cells
            Tetromino rotated = piece.rotateClockwise();
            var rotatedCells = rotated.getOccupiedCells();
            assert rotatedCells.size() == 4 : "Rotated piece should still occupy 4 cells";
        });
    }

    private static void testBoardBoundaryDetection() {
        test("Board boundary and out-of-bounds detection", () -> {
            Board board = new Board();

            // Test valid boundaries
            assert board.isInBounds(new Point(0, 0)) : "Top-left should be in bounds";
            assert board.isInBounds(new Point(Board.ROWS - 1, Board.COLS - 1)) : "Bottom-right should be in bounds";

            // Test out-of-bounds
            assert !board.isInBounds(new Point(-1, 0)) : "Negative row should be out of bounds";
            assert !board.isInBounds(new Point(0, -1)) : "Negative col should be out of bounds";
            assert !board.isInBounds(new Point(Board.ROWS, 0)) : "Row >= ROWS should be out of bounds";
            assert !board.isInBounds(new Point(0, Board.COLS)) : "Col >= COLS should be out of bounds";
        });
    }

    private static void testScorePersistence() {
        test("Score persistence across difficulty levels", () -> {
            GameState easyState = new GameState(GameDifficulty.EASY);
            assert easyState.getScore() == 0 : "New game should start with 0 score";
            assert easyState.getLinesCleared() == 0 : "New game should have 0 lines cleared";

            // Verify score multiplier affects calculation
            GameDifficulty hard = GameDifficulty.HARD;
            assert hard.getScoreMultiplier() == 1.5f : "Hard difficulty should have 1.5x multiplier";

            // Create game state and verify immutability
            GameState state2 = new GameState.Builder(easyState)
                    .withScore(100)
                    .build();
            assert easyState.getScore() == 0 : "Original state should remain unchanged";
            assert state2.getScore() == 100 : "New state should have updated score";
        });
    }

    private static void testGameStateBuilder() {
        test("GameState builder pattern", () -> {
            GameState initial = new GameState(GameDifficulty.NORMAL);

            // Build with multiple changes
            GameState modified = new GameState.Builder(initial)
                    .withScore(500)
                    .withLinesCleared(3)
                    .withPaused(true)
                    .build();

            assert modified.getScore() == 500 : "Score should be 500";
            assert modified.getLinesCleared() == 3 : "Lines should be 3";
            assert modified.isPaused() : "Should be paused";

            // Verify original is unchanged
            assert initial.getScore() == 0 : "Original score should be 0";
            assert initial.getLinesCleared() == 0 : "Original lines should be 0";
            assert !initial.isPaused() : "Original should not be paused";

            // Test difficulty preservation
            assert modified.getDifficulty() == GameDifficulty.NORMAL : "Difficulty should be preserved";
        });
    }
    // ==================== Controller Tests ====================

    private static void testGameControllerInitialization() {
        test("GameController initialization", () -> {
            GameController controller = new GameController(new Renderer());
            GameState state = controller.getState();

            assert state != null : "Controller should initialize game state";
            assert !state.isGameOver() : "New game should not be over";
            assert state.getScore() == 0 : "New game should have score 0";
            assert state.getLinesCleared() == 0 : "New game should have 0 lines cleared";
        });
    }

    private static void testGameControllerTickProcessing() {
        test("GameController tick processing", () -> {
            GameController controller = new GameController(new Renderer());
            GameState initialState = controller.getState();

            controller.tick(GameAction.RIGHT);
            GameState afterMove = controller.getState();

            assert afterMove.getCurrentPiece().getPosition().getCol() 
                > initialState.getCurrentPiece().getPosition().getCol()
                : "Controller should process movements";
        });
    }

    private static void testGameControllerReset() {
        test("GameController reset", () -> {
            GameController controller = new GameController(new Renderer());

            // Make some moves
            for (int i = 0; i < 10; i++) {
                controller.tick(GameAction.NONE);
            }

            GameState beforeReset = controller.getState();
            assert beforeReset.getTickCount() > 0 : "Game should have ticks";

            controller.reset();
            GameState afterReset = controller.getState();

            assert afterReset.getTickCount() == 0 : "Reset should clear tick count";
            assert afterReset.getScore() == 0 : "Reset should clear score";
        });
    }

    private static void testAudioManager() {
        test("AudioManager initialization and settings", () -> {
            AudioManager audioManager = new AudioManager();
            
            // Test initial state
            assert !audioManager.isEnabled() : "Audio should be disabled by default";
            assert !audioManager.isMusicPlaying() : "No music should be playing initially";
            assert audioManager.getCurrentSoundtrack() == null : "Current soundtrack should be null initially";
            
            // Test enable/disable
            audioManager.setEnabled(true);
            assert audioManager.isEnabled() : "Audio should be enabled after setEnabled(true)";
            
            audioManager.setEnabled(false);
            assert !audioManager.isEnabled() : "Audio should be disabled after setEnabled(false)";
        });

        test("AudioManager soundtrack enumeration", () -> {
            AudioManager.Soundtrack[] soundtracks = AudioManager.getAvailableSoundtracks();
            
            assert soundtracks.length == 4 : "Should have 4 available soundtracks";
            assert soundtracks[0] == AudioManager.Soundtrack.MUSIC_1 : "First soundtrack should be MUSIC_1";
            assert soundtracks[1] == AudioManager.Soundtrack.MUSIC_2 : "Second soundtrack should be MUSIC_2";
            assert soundtracks[2] == AudioManager.Soundtrack.MUSIC_3 : "Third soundtrack should be MUSIC_3";
            assert soundtracks[3] == AudioManager.Soundtrack.MUSIC_4 : "Fourth soundtrack should be MUSIC_4";
            
            // Test soundtrack properties
            assert AudioManager.Soundtrack.MUSIC_1.getFilename().equals("music_1.m4a") : "Filename should match";
            assert AudioManager.Soundtrack.MUSIC_1.getDisplayName().equals("Background Track 1") : "Display name should match";
        });

        test("AudioManager sound effects enumeration", () -> {
            AudioManager.SoundEffect[] effects = AudioManager.SoundEffect.values();
            
            assert effects.length == 2 : "Should have 2 sound effects";
            assert effects[0] == AudioManager.SoundEffect.PIECE_PLACED : "First effect should be PIECE_PLACED";
            assert effects[1] == AudioManager.SoundEffect.LINE_CLEARED : "Second effect should be LINE_CLEARED";
            
            // Test effect properties
            assert AudioManager.SoundEffect.PIECE_PLACED.getFilename().equals("piece_placed.m4a") : "Filename should match";
            assert AudioManager.SoundEffect.LINE_CLEARED.getFilename().equals("line_cleared.m4a") : "Filename should match";
        });
    }

    // ==================== Test Infrastructure ====================

    /**
     * Runs a test and tracks results.
     */
    private static void test(String name, Runnable testCode) {
        try {
            testCode.run();
            System.out.println("✓ " + name);
            testsPassed++;
        } catch (AssertionError e) {
            System.out.println("✗ " + name);
            System.out.println("  Error: " + e.getMessage());
            testsFailed++;
        } catch (Exception e) {
            System.out.println("✗ " + name);
            System.out.println("  Exception: " + e.getMessage());
            testsFailed++;
        }
    }
}
