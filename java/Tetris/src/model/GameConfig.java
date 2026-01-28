package model;

import java.io.*;
import java.util.*;

/**
 * Configuration manager for Tetris game preferences.
 * Handles loading and saving user preferences to disk.
 *
 * Persistent settings include:
 * - Theme preference (light, dark, colorful)
 * - Sound settings
 * - Difficulty preference
 * - Auto-save mode
 *
 * @author Tetris Engine
 */
public class GameConfig {
    private static final String CONFIG_DIR = ".tetris";
    private static final String CONFIG_FILE = ".tetris/tetris.config";

    /**
     * Enumeration of available color themes.
     */
    public enum Theme {
        LIGHT("Light - White text on dark background"),
        DARK("Dark - Low contrast for easy viewing"),
        COLORFUL("Colorful - Rich colors and contrast");

        private final String description;

        Theme(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private Theme theme;
    private boolean soundEnabled;
    private GameDifficulty defaultDifficulty;
    private boolean autoSaveEnabled;

    /**
     * Constructs a GameConfig with default settings.
     */
    public GameConfig() {
        this.theme = Theme.LIGHT;
        this.soundEnabled = false;
        this.defaultDifficulty = GameDifficulty.NORMAL;
        this.autoSaveEnabled = true;
        loadConfig();
    }

    /**
     * Returns the current theme.
     *
     * @return the Theme
     */
    public Theme getTheme() {
        return theme;
    }

    /**
     * Sets the theme and saves configuration.
     *
     * @param theme the new theme
     */
    public void setTheme(Theme theme) {
        this.theme = theme;
        saveConfig();
    }

    /**
     * Returns whether sound is enabled.
     *
     * @return true if sound enabled
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * Sets sound enabled state and saves configuration.
     *
     * @param soundEnabled whether to enable sound
     */
    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
        saveConfig();
    }

    /**
     * Returns the default difficulty level.
     *
     * @return the default GameDifficulty
     */
    public GameDifficulty getDefaultDifficulty() {
        return defaultDifficulty;
    }

    /**
     * Sets the default difficulty and saves configuration.
     *
     * @param difficulty the new default difficulty
     */
    public void setDefaultDifficulty(GameDifficulty difficulty) {
        this.defaultDifficulty = difficulty;
        saveConfig();
    }

    /**
     * Returns whether auto-save is enabled.
     *
     * @return true if auto-save enabled
     */
    public boolean isAutoSaveEnabled() {
        return autoSaveEnabled;
    }

    /**
     * Sets auto-save enabled state and saves configuration.
     *
     * @param autoSaveEnabled whether to enable auto-save
     */
    public void setAutoSaveEnabled(boolean autoSaveEnabled) {
        this.autoSaveEnabled = autoSaveEnabled;
        saveConfig();
    }

    /**
     * Loads configuration from disk.
     */
    private void loadConfig() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    switch (key) {
                        case "theme":
                            try {
                                this.theme = Theme.valueOf(value);
                            } catch (IllegalArgumentException e) {
                                // Keep default
                            }
                            break;
                        case "soundEnabled":
                            this.soundEnabled = Boolean.parseBoolean(value);
                            break;
                        case "defaultDifficulty":
                            try {
                                this.defaultDifficulty = GameDifficulty.valueOf(value);
                            } catch (IllegalArgumentException e) {
                                // Keep default
                            }
                            break;
                        case "autoSaveEnabled":
                            this.autoSaveEnabled = Boolean.parseBoolean(value);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load config: " + e.getMessage());
        }
    }

    /**
     * Saves configuration to disk.
     */
    private void saveConfig() {
        try {
            File dir = new File(CONFIG_DIR);
            dir.mkdirs();

            try (PrintWriter writer = new PrintWriter(new FileWriter(CONFIG_FILE))) {
                writer.println("theme=" + theme.name());
                writer.println("soundEnabled=" + soundEnabled);
                writer.println("defaultDifficulty=" + defaultDifficulty.name());
                writer.println("autoSaveEnabled=" + autoSaveEnabled);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not save config: " + e.getMessage());
        }
    }

    /**
     * Returns the ANSI escape code for the current theme's main color.
     *
     * @return ANSI color code
     */
    public String getThemeColor() {
        switch (theme) {
            case DARK:
                return "\033[90m"; // Gray
            case COLORFUL:
                return "\033[36m"; // Cyan
            case LIGHT:
            default:
                return "\033[37m"; // White
        }
    }

    /**
     * Resets configuration to defaults.
     */
    public void reset() {
        this.theme = Theme.LIGHT;
        this.soundEnabled = false;
        this.defaultDifficulty = GameDifficulty.NORMAL;
        this.autoSaveEnabled = true;
        saveConfig();
    }

    @Override
    public String toString() {
        return String.format("GameConfig{theme=%s, sound=%b, difficulty=%s, autoSave=%b}",
                theme.name(), soundEnabled, defaultDifficulty.name(), autoSaveEnabled);
    }
}
