package model;

/**
 * Tracks game timing and personal records.
 * Immutable class for recording and comparing game durations.
 *
 * @author Tetris Engine
 */
public class GameTimer {
    private final long startTimeMillis;
    private final long elapsedMillis;
    private static long personalRecordMillis = Long.MAX_VALUE;

    /**
     * Constructs a GameTimer at the current time.
     */
    public GameTimer() {
        this.startTimeMillis = System.currentTimeMillis();
        this.elapsedMillis = 0;
    }

    /**
     * Constructs a GameTimer with specified elapsed time.
     *
     * @param elapsedMillis the elapsed time in milliseconds
     */
    private GameTimer(long startTimeMillis, long elapsedMillis) {
        this.startTimeMillis = startTimeMillis;
        this.elapsedMillis = elapsedMillis;
    }

    /**
     * Returns a new GameTimer with updated elapsed time.
     * The original timer remains unchanged (immutability).
     *
     * @return a new GameTimer with current elapsed time
     */
    public GameTimer update() {
        long now = System.currentTimeMillis();
        long elapsed = now - startTimeMillis;
        return new GameTimer(startTimeMillis, elapsed);
    }

    /**
     * Returns the elapsed time in milliseconds.
     *
     * @return elapsed milliseconds
     */
    public long getElapsedMillis() {
        return elapsedMillis;
    }

    /**
     * Returns the elapsed time in seconds.
     *
     * @return elapsed seconds
     */
    public double getElapsedSeconds() {
        return elapsedMillis / 1000.0;
    }

    /**
     * Returns the elapsed time formatted as MM:SS.
     *
     * @return formatted time string
     */
    public String getFormattedTime() {
        long seconds = elapsedMillis / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    /**
     * Checks if this time is a new personal record.
     *
     * @return true if elapsed time is less than personal record
     */
    public boolean isNewRecord() {
        return elapsedMillis < personalRecordMillis;
    }

    /**
     * Updates the personal record if this time qualifies.
     *
     * @return true if personal record was updated
     */
    public boolean updatePersonalRecord() {
        if (isNewRecord()) {
            personalRecordMillis = elapsedMillis;
            return true;
        }
        return false;
    }

    /**
     * Returns the current personal record in milliseconds.
     *
     * @return personal record in milliseconds, or Long.MAX_VALUE if none
     */
    public static long getPersonalRecordMillis() {
        return personalRecordMillis;
    }

    /**
     * Returns the personal record formatted as MM:SS.
     *
     * @return formatted record time, or "N/A" if no record
     */
    public static String getFormattedPersonalRecord() {
        if (personalRecordMillis == Long.MAX_VALUE) {
            return "N/A";
        }
        long seconds = personalRecordMillis / 1000;
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

    /**
     * Resets the personal record.
     */
    public static void resetPersonalRecord() {
        personalRecordMillis = Long.MAX_VALUE;
    }

    @Override
    public String toString() {
        return getFormattedTime();
    }
}
