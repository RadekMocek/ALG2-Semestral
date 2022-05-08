package app;

import utils.TimeTools;
import com.mpatric.mp3agic.*;
import java.io.IOException;
import utils.ITagEditable;

/**
 * Třída uchovávající jeden mp3 soubor a umožňující editaci jeho tagů
 * @author Radek Mocek
 */
public class AudioFile implements ITagEditable {

    private Mp3File file;
    private ID3v2 tag;

    /**
     * Konstruktor
     * @param path Absolutní cesta k mp3 souboru (String)
     */
    public AudioFile(String path) {
        try {
            file = new Mp3File(path);
            tag = file.getId3v2Tag();
        } catch (IOException | UnsupportedTagException | InvalidDataException ex) {
            System.out.println("Chyba č.1: " + ex);
        }
    }

    // ###############
    // ### Gettery ###
    // ###############

    /**
     * Vrací název umělce uložený v ID3v2 tagu
     * @return String
     */
    @Override
    public String getArtist() {
        return tag.getArtist();
    }

    /**
     * Vrací název alba uložený v ID3v2 tagu
     * @return String
     */
    @Override
    public String getAlbum() {
        return tag.getAlbum();
    }

    /**
     * Vrací název skladby uložený v ID3v2 tagu
     * @return String
     */
    @Override
    public String getTitle() {
        return tag.getTitle();
    }

    // ########################
    // ### Metody pro výpis ###
    // ########################

    /**
     * Vrací zformátovaný String s informacemi z tagu souboru pro výpis více skladeb pod sebou
     * @param artistLen
     * @param albumLen
     * @param titleLen
     * @return String
     */
    @Override
    public String toStringFormatted(int artistLen, int albumLen, int titleLen) {
        String artist = tag.getArtist();
        String year = tag.getYear();
        String album = tag.getAlbum();

        String trackNum = tag.getTrack();
        if (trackNum != null && trackNum.contains("/")) trackNum = trackNum.split("/")[0];

        String title = tag.getTitle();
        String absolutePath = file.getFilename();

        long seconds = file.getLengthInSeconds();
        String duration = TimeTools.longToString(seconds);

        String format = "%-" + artistLen + "s %-5.5s %-" + albumLen + "s %3s. %-" + titleLen + "s %8s %s";
        return String.format(format, artist, year, album, trackNum, title, duration, absolutePath);
    }

    /**
     * Vrací zformátovaný String s informacemi z tagu souboru pro výpis jedné konkrétní skladby
     * @return
     */
    @Override
    public String toString() {
        String absolutePath = file.getFilename();
        String artist = tag.getArtist();
        String year = tag.getYear();
        String album = tag.getAlbum();
        String trackNum = tag.getTrack();
        String title = tag.getTitle();
        String rtrn = """
                      %s
                      Interpret : %s
                      Rok       : %s
                      Album     : %s
                      Stopa č.  : %s
                      Skladba   : %s
                      """.formatted(absolutePath, artist, year, album, trackNum, title);
        return rtrn;
    }

}
