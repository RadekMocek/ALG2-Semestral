package utils;

import java.time.Duration;

/**
 * Pomocná třída pro práci s časovými údaji
 * @author Radek Mocek
 */
public final class TimeTools {

    private TimeTools() {}

    /**
     * Pro zadaný počet vteřin vrátí String ve formátu "(hh):(m)m:ss", který je často používaný v hudebním software a je youtube-timestamp-friendly
     * @param totalSeconds long, počet vteřin
     * @return String s časem, sekundy převedené na formát (hh):(m)m:ss
     */
    public static String longToString(long totalSeconds) {
        if (totalSeconds < 0) {
            return "0:00";
        }

        Duration duration = Duration.ofSeconds(totalSeconds);
        long hoursLong = duration.toHours();
        long minutesLong = duration.toMinutesPart();
        long secondsLong = duration.toSecondsPart();

        String hours = (hoursLong == 0) ? "" : Long.toString(hoursLong) + ":";
        String minutes = Long.toString(minutesLong);
        if (hoursLong != 0 && minutes.length() == 1) {
            minutes = "0" + minutes;
        }
        String seconds = Long.toString(secondsLong);
        if (seconds.length() == 1) {
            seconds = "0" + seconds;
        }
        return String.format("%s%s:%s", hours, minutes, seconds);
    }

    // --------------------------------------
    // Testovací účely
    /*
    public static void main(String[] args) {
        long l = 3661;
        System.out.println(longToString(l));
    }
    */
}
