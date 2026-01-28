# Tetris Clone

A professional-grade Tetris clone implementation in Java, demonstrating best practices in software architecture, design patterns, and object-oriented programming.

## Features

- **Classic Tetris Gameplay**: Full implementation of Tetris mechanics including piece rotation, collision detection, and line clearing
- **Dual Interface Modes**:
  - Terminal-based UI with Unicode rendering (cross-platform, lightweight)
  - Swing GUI with graphical board rendering (intuitive, visual feedback)
- **Difficulty Levels**: Four difficulty levels (EASY, NORMAL, HARD, EXTREME) with progressive gravity and score multipliers
- **Wall Kick System**: Advanced piece rotation with wall kick mechanics for better playability
- **Game Statistics**: Tracks scores, lines cleared, and game duration with persistent high score storage
- **Pause/Resume**: Full pause functionality with formatted timer display
- **Configuration System**: Persistent user preferences including theme selection and default difficulty
- **Demo Mode**: Automated AI player for continuous demonstration
- **Personal Records**: Timer and record tracking system for competitive play

## Architecture

This project follows the **Model-View-Controller (MVC)** pattern with emphasis on:

- **Immutability**: All core game objects are immutable, ensuring thread safety and predictable behavior
- **Builder Pattern**: Complex object construction through fluent builders
- **Pure Functions**: Game engine logic uses functional transformations
- **Separation of Concerns**: Clear boundaries between game logic, rendering, and user interaction
- **Polymorphic View Layer**: Multiple view implementations (Terminal, Swing) sharing a common interface

## Installation

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- macOS, Linux, or Windows with bash shell

### Build from Source

```bash
./build.sh
```

This will compile all source files and create executable JAR files in `dist/`.

## Usage

### Terminal Mode (Default)

```bash
./run.sh
```

### GUI Mode (Swing)

```bash
./run.sh --gui
```

### Game Controls

Both modes support the same controls:

| Action | Terminal Key | GUI Key | Function |
|--------|--------------|---------|----------|
| Move Left | A | A or LEFT | Move piece left |
| Move Right | D | D or RIGHT | Move piece right |
| Soft Drop | S | S or DOWN | Accelerate descent |
| Rotate | W | W or UP | Rotate clockwise (wall kick) |
| Hard Drop | SPACE | SPACE | Instant drop to bottom |
| Pause/Resume | P | P | Pause or resume game |
| Quit | Q | Q or ESC | Exit the game |

## Difficulty Levels

| Level | Gravity | Multiplier |
|-------|---------|-----------|
| EASY | 30 ticks | 1.0x |
| NORMAL | 20 ticks | 1.5x |
| HARD | 12 ticks | 2.0x |
| EXTREME | 5 ticks | 3.0x |

## Testing

Run the comprehensive test suite:

```bash
java -cp dist/tetris-test.jar TetrisGameTest
```

**Test Coverage: 23 tests** covering all core functionality including:
- Point and Tetromino mechanics
- Board collision detection and line clearing
- Game engine tick processing and gravity
- Wall kick rotations
- Game state management and statistics
- Controller input handling
- High score persistence
- Demo mode AI

## Project Structure

```
src/
├── model/              # Game data and business logic
│   ├── Point.java
│   ├── Tetromino.java
│   ├── Board.java
│   ├── GameState.java
│   ├── GameAction.java
│   ├── GameDifficulty.java
│   ├── GameTimer.java
│   ├── GameConfig.java
│   ├── GameStatistics.java
│   ├── HighScoreManager.java
│   └── GameException*.java
├── engine/             # Game logic and AI
│   ├── GameEngine.java
│   └── GameDemo.java
├── view/               # Rendering and display
│   ├── GameView.java (interface)
│   ├── Renderer.java (terminal)
│   └── SwingRenderer.java (GUI)
├── controller/         # Game orchestration
│   └── GameController.java
├── Tetris.java        # Entry point
└── TetrisGameTest.java # Test suite
```

## Build Scripts

- **build.sh** - Compiles source, creates JAR with manifest, runs tests
- **run.sh** - Launches the game (terminal or GUI mode)

## License

MIT License - See [LICENSE](LICENSE) file for details.

## Author

Created as a demonstration of professional software engineering practices in Java.
