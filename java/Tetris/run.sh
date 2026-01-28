#!/bin/bash

# Tetris Run Script
# Launches the Tetris game

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

DIST_DIR="dist"
JAR_FILE="$DIST_DIR/tetris.jar"

echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║        Welcome to Tetris Clone         ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════╝${NC}"
echo

# Check if JAR exists, build if necessary
if [ ! -f "$JAR_FILE" ]; then
    echo -e "${YELLOW}JAR file not found. Building...${NC}"
    echo
    ./build.sh
    echo
fi

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo -e "${RED}Error: Java runtime not found. Please install Java.${NC}"
    exit 1
fi

# Run the game
echo -e "${GREEN}Starting Tetris...${NC}"
echo -e "${YELLOW}Controls:${NC}"
echo "  LEFT   - Move left (A)"
echo "  RIGHT  - Move right (D)"
echo "  DOWN   - Move down (S)"
echo "  ROTATE - Rotate piece (W)"
echo "  DROP   - Drop piece (SPACE)"
echo "  PAUSE  - Pause/Resume (P)"
echo "  QUIT   - Exit game (Q)"
echo

java -cp "$JAR_FILE" Tetris "$@"
EXIT_CODE=$?

echo
if [ $EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}Thanks for playing Tetris!${NC}"
else
    echo -e "${RED}Game exited with error code: $EXIT_CODE${NC}"
fi

exit $EXIT_CODE
