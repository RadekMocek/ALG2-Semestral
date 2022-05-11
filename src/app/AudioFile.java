package app;

import utils.TimeTools;
import com.mpatric.mp3agic.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
     * @throws com.mpatric.mp3agic.UnsupportedTagException
     * @throws java.io.IOException
     * @throws com.mpatric.mp3agic.InvalidDataException
     * @throws java.lang.IllegalArgumentException
     */
    public AudioFile(String path) throws UnsupportedTagException, IOException, InvalidDataException, IllegalArgumentException {
        // Inicializace souboru
        file = new Mp3File(path);
        // Inicializace tagu
        if (file.hasId3v2Tag()) {
            tag = file.getId3v2Tag();
        }
        else {
            tag = new ID3v24Tag();
            file.setId3v2Tag(tag);
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
        if (year == null) year = "-";

        String album = tag.getAlbum();

        String trackNum = tag.getTrack();
        if (trackNum != null) {
            if (trackNum.contains("/")) trackNum = trackNum.split("/")[0];
        }
        else {
            trackNum = "-";
        }

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

    // ####################
    // ### Editace tagů ###
    // ####################

    /**
     * Změní název intepreta
     * @param newArtist String, nový název interpreta
     */
    @Override
    public void changeArtist(String newArtist) {
        tag.setArtist(newArtist);
        saveTags();

    }

    /**
     * Uloží tagy do nového souboru, ten pak přejmenuje na název toho starého a tím ho přepíše
     */
    private void saveTags() {
        try {
            String tempFilename = file.getFilename() + "edited";
            file.save(tempFilename);
            Path source = Paths.get(tempFilename);
            Files.move(source, source.resolveSibling(file.getFilename()), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException | NotSupportedException | IllegalArgumentException ex) {
            throw new RuntimeException("Chyba při ukládání tagu: " + ex);
        }
    }

}
