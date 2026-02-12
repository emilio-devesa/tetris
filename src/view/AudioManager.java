package view;

import java.io.File;
import javax.sound.sampled.*;

/**
 * Manages audio playback for game sounds and background music.
 * Supports background music selection and sound effect playback.
 *
 * Audio features:
 * - Background music from assets/audio/music/ directory
 * - Sound effects from assets/audio/sfx/ directory
 * - Optional audio (disabled by default, enabled via settings)
 * - Non-blocking audio playback
 *
 * @author Tetris Engine
 */
public class AudioManager {
    private static final String MUSIC_DIR = "assets/audio/music";
    private static final String SFX_DIR = "assets/audio/sfx";
    private boolean enabled;
    private Clip currentMusic;
    private Soundtrack currentSoundtrack;

    /**
     * Enumeration of available background music soundtracks.
     */
    public enum Soundtrack {
        MUSIC_1("music_1.m4a", "Loginska"),
        MUSIC_2("music_2.m4a", "Bradinsky"),
        MUSIC_3("music_3.m4a", "Karinka"),
        MUSIC_4("music_4.m4a", "Troika");

        private final String filename;
        private final String displayName;

        Soundtrack(String filename, String displayName) {
            this.filename = filename;
            this.displayName = displayName;
        }

        public String getFilename() {
            return filename;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Enumeration of sound effects.
     */
    public enum SoundEffect {
        PIECE_PLACED("piece_placed.m4a"),
        LINE_CLEARED("line_cleared.m4a");

        private final String filename;

        SoundEffect(String filename) {
            this.filename = filename;
        }

        public String getFilename() {
            return filename;
        }
    }

    /**
     * Constructs an AudioManager with audio disabled by default.
     */
    public AudioManager() {
        this.enabled = false;
        this.currentSoundtrack = null;
    }

    /**
     * Enables or disables audio playback.
     *
     * @param enabled true to enable audio, false to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled && currentMusic != null && currentMusic.isRunning()) {
            stopMusic();
        }
    }

    /**
     * Checks if audio is enabled.
     *
     * @return true if audio is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Plays a sound effect.
     *
     * @param effect the sound effect to play
     */
    public void playSound(SoundEffect effect) {
        if (!enabled) {
            return;
        }

        new Thread(() -> {
            try {
                // Try WAV format first (Java native support)
                File soundFile = new File(SFX_DIR, effect.getFilename().replace(".m4a", ".wav"));
                
                // If WAV doesn't exist, try M4A (will likely fail but worth trying)
                if (!soundFile.exists()) {
                    soundFile = new File(SFX_DIR, effect.getFilename());
                }
                
                if (!soundFile.exists()) {
                    System.err.println("Sound file not found: " + soundFile.getAbsolutePath());
                    return;
                }

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (Exception e) {
                System.err.println("Error playing sound effect: " + e.getMessage());
                System.err.println("Make sure audio files are in WAV format, not M4A.");
                System.err.println("See AUDIO_SETUP.md for conversion instructions.");
            }
        }).start();
    }

    /**
     * Plays background music.
     *
     * @param soundtrack the soundtrack to play
     */
    public void playMusic(Soundtrack soundtrack) {
        if (!enabled) {
            return;
        }

        // Stop current music if playing
        if (currentMusic != null && currentMusic.isRunning()) {
            stopMusic();
        }

        new Thread(() -> {
            try {
                // Try WAV format first (Java native support)
                File musicFile = new File(MUSIC_DIR, soundtrack.getFilename().replace(".m4a", ".wav"));
                
                // If WAV doesn't exist, try M4A (will likely fail but worth trying)
                if (!musicFile.exists()) {
                    musicFile = new File(MUSIC_DIR, soundtrack.getFilename());
                }
                
                if (!musicFile.exists()) {
                    System.err.println("Audio file not found: " + musicFile.getAbsolutePath());
                    return;
                }

                AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
                currentMusic = AudioSystem.getClip();
                currentMusic.open(audioStream);
                currentMusic.loop(Clip.LOOP_CONTINUOUSLY);
                currentMusic.start();
                currentSoundtrack = soundtrack;
            } catch (Exception e) {
                System.err.println("Error playing music '" + soundtrack.getDisplayName() + "': " + e.getMessage());
                System.err.println("Make sure audio files are in WAV format, not M4A.");
                System.err.println("See AUDIO_SETUP.md for conversion instructions.");
            }
        }).start();
    }

    /**
     * Stops background music.
     */
    public void stopMusic() {
        if (currentMusic != null) {
            try {
                currentMusic.stop();
                currentMusic.close();
                currentMusic = null;
                currentSoundtrack = null;
            } catch (Exception e) {
                // Silently fail
            }
        }
    }

    /**
     * Gets the currently playing soundtrack.
     *
     * @return the current soundtrack, or null if none playing
     */
    public Soundtrack getCurrentSoundtrack() {
        return currentSoundtrack;
    }

    /**
     * Checks if music is currently playing.
     *
     * @return true if music is playing
     */
    public boolean isMusicPlaying() {
        return currentMusic != null && currentMusic.isRunning();
    }

    /**
     * Returns all available soundtracks.
     *
     * @return array of all soundtrack options
     */
    public static Soundtrack[] getAvailableSoundtracks() {
        return Soundtrack.values();
    }
}
