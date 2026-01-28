# Tetris Clone

A professional-grade Tetris clone implementation in Java, demonstrating best practices in software architecture, design patterns, and object-oriented programming.

## Features

- **Classic Tetris Gameplay**: Full implementation of Tetris mechanics including piece rotation, collision detection, and line clearing
- **Difficulty Levels**: Four difficulty levels (EASY, NORMAL, HARD, EXTREME) with progressive gravity and score multipliers
- **Wall Kick System**: Advanced piece rotation with wall kick mechanics for better playability
- **Game Statistics**: Tracks scores, lines cleared, and game duration with persistent high score storage
- **Pause/Resume**: Full pause functionality with formatted timer display
- **Configuration System**: Persistent user preferences including theme selection and default difficulty
- **Demo Mode**: Automated AI player for continuous demonstration
- **Terminal-Based UI**: Cross-platform terminal rendering using Unicode characters

## Architecture

This project follows the **Model-View-Controller (MVC)** pattern with emphasis on:

- **Immutability**: All core game objects are immutable, ensuring thread safety and predictable behavior
- **Builder Pattern**: Complex object construction through fluent builders
- **Pure Functions**: Game engine logic uses functional transformations
- **Separation of Concerns**: Clear boundaries between game logic, rendering, and user interaction

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

### Launch the Game

```bash
./run.sh
```

### Game Controls

| Control | Key | Action |
|---------|-----|--------|
| Move Left | A | Move current piece left |
| Move Right | D | Move current piece right |
| Move Down | S | Soft drop (accelerate descent) |
| Rotate | W | Rotate current piece |
| Instant Drop | SPACE | Hard drop to bottom |
| Pause/Resume | P | Pause or resume game |
| Quit | Q | Exit the game |

## Difficulty Levels

| Level | Gravity (ticks) | Score Multiplier |
|-------|-----------------|------------------|
| EASY | 30 | 1.0x |
| NORMAL | 20 | 1.5x |
| HARD | 12 | 2.0x |
| EXTREME | 5 | 3.0x |

## Testing

Run the comprehensive test suite:

```bash
java -cp dist/tetris-test.jar TetrisGameTest
```

**Test Coverage: 23 tests** covering all core functionality.

## License

MIT License - See [LICENSE](LICENSE) file for details.

## Author

Created as a demonstration of professional software engineering practices in Java.
