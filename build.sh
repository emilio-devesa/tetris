#!/bin/bash

# Tetris Build Script
# Compiles all Java source files and creates an executable JAR

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== Tetris Build Script ===${NC}"
echo

# Check if Java compiler is available
if ! command -v javac &> /dev/null; then
    echo -e "${RED}Error: javac not found. Please install Java Development Kit.${NC}"
    exit 1
fi

# Create build directory
BUILD_DIR="build"
SRC_DIR="src"
DIST_DIR="dist"

echo -e "${YELLOW}Creating build directories...${NC}"
mkdir -p "$BUILD_DIR"
mkdir -p "$DIST_DIR"

# Compile all Java files
echo -e "${YELLOW}Compiling Java source files...${NC}"
javac -d "$BUILD_DIR" \
    "$SRC_DIR"/model/*.java \
    "$SRC_DIR"/engine/*.java \
    "$SRC_DIR"/controller/*.java \
    "$SRC_DIR"/view/*.java \
    "$SRC_DIR"/Tetris.java \
    "$SRC_DIR"/TetrisGameTest.java 2>&1

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Compilation successful${NC}"
else
    echo -e "${RED}✗ Compilation failed${NC}"
    exit 1
fi

# Create manifest file
echo -e "${YELLOW}Creating JAR manifest...${NC}"
cat > "$BUILD_DIR/MANIFEST.MF" << EOF
Manifest-Version: 1.0
Main-Class: Tetris
Class-Path: .
Created-By: Tetris Build Script
Implementation-Version: 1.0.0
EOF

# Create JAR file
echo -e "${YELLOW}Creating JAR file...${NC}"
(cd "$BUILD_DIR" && jar cfm ../dist/tetris.jar MANIFEST.MF \
    model/*.class \
    engine/*.class \
    controller/*.class \
    view/*.class \
    Tetris.class \
    TetrisGameTest.class)

if [ -f "$DIST_DIR/tetris.jar" ]; then
    echo -e "${GREEN}✓ JAR created: $DIST_DIR/tetris.jar${NC}"
else
    echo -e "${RED}✗ Failed to create JAR${NC}"
    exit 1
fi

# Create test runner JAR
echo -e "${YELLOW}Creating test runner JAR...${NC}"
(cd "$BUILD_DIR" && jar cfm ../dist/tetris-test.jar MANIFEST.MF \
    model/*.class \
    engine/*.class \
    controller/*.class \
    view/*.class \
    TetrisGameTest.class)

if [ -f "$DIST_DIR/tetris-test.jar" ]; then
    echo -e "${GREEN}✓ Test JAR created: $DIST_DIR/tetris-test.jar${NC}"
else
    echo -e "${RED}✗ Failed to create test JAR${NC}"
    exit 1
fi

echo
echo -e "${GREEN}=== Build Complete ===${NC}"
echo -e "${YELLOW}To run the game:${NC}"
echo "  ./run.sh"
echo
echo -e "${YELLOW}To run tests:${NC}"
echo "  java -cp dist/tetris-test.jar TetrisGameTest"
echo
