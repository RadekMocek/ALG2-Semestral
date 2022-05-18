package app;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v23Tag;
//import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import utils.TimeTools;
import java.io.File;
import java.io.IOException;
import utils.ITagEditable;
import utils.TagField;

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
            tag = new ID3v23Tag();
            file.setId3v2Tag(tag);
        }
        // S ID3v1 tagy nevyjednáváme ((these days doporučuje se buď jim nastavovat identickou hodnotu jako v2 tagům, nebo je smazat))
        if (file.hasId3v1Tag()) {
            file.removeId3v1Tag();
        }
    }

    /**
     * Při přejmenování souboru je potřeba aktualizovat jeho cestu, aby bylo možné ho dále editovat
     * @param newFileName String
     */
    @Override
    public void updatePath(String newFileName) {
        try {
            String oldPath = file.getFilename();
            file = new Mp3File(oldPath.substring(0, oldPath.lastIndexOf(File.separator)) + File.separator + newFileName);
            tag = file.getId3v2Tag();
        } catch (IOException | UnsupportedTagException | InvalidDataException ex) {
            throw new RuntimeException("Chyba při aktulaizování cesty k souboru.");
        }
    }

    // ###############
    // ### Gettery ###
    // ###############

    /**
     * Vratí cestu k souboru
     * @return String
     */
    @Override
    public String getAbsolutePath() {
        return file.getFilename();
    }

    /**
     * Vrátí délku skladby v sekundách
     * @return long
     */
    @Override
    public long getLengthInSeconds() {
        return file.getLengthInSeconds();
    }

    /**
     * Vrací název interpreta uložený v ID3v2 tagu
     * @return String
     */
    @Override
    public String getArtist() {
        return tag.getArtist();
    }

    /**
     * Vrací rok uložený v ID3v2 tagu
     * @return String
     */
    @Override
    public String getYear() {
        return tag.getYear();
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
     * Vrací číslo stopy uložené v ID3v2 tagu
     * @return String
     */
    @Override
    public String getTrackNum() {
        return tag.getTrack();
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
     * @param artistLen int, Počet znaků interpreta s nejdelším názvem ve výpisu
     * @param albumLen int, Počet znaků alba s nejdelším názvem ve výpisu
     * @param titleLen int, Počet znaků skladby s nejdelším názvem ve výpisu
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
     * @return String
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
        if ("".equals(newArtist)) {
            clearTagFieldValue(TagField.artist);
        }
        else {
            tag.setArtist(newArtist);
        }
        saveTags();
    }

    /**
     * Změní rok
     * @param newYear String, nový rok
     */
    @Override
    public void changeYear(String newYear) {
        if ("".equals(newYear)) {clearTagFieldValue(TagField.year);}
        else {tag.setYear(newYear);}
        saveTags();
    }

    /**
     * Změní název alba
     * @param newAlbum String, nové album
     */
    @Override
    public void changeAlbum(String newAlbum) {
        if ("".equals(newAlbum)) {clearTagFieldValue(TagField.album);}
        else {tag.setAlbum(newAlbum);}
        saveTags();
    }

    /**
     * Změní číslo stopy
     * @param changeTrackNum String, nové číslo stopy
     */
    @Override
    public void changeTrackNum(String changeTrackNum) {
        if ("".equals(changeTrackNum)) {clearTagFieldValue(TagField.trackNum);}
        else {tag.setTrack(changeTrackNum);}
        saveTags();
    }

    /**
     * Změní název skladby
     * @param newTitle String, nový název skladby
     */
    @Override
    public void changeTitle(String newTitle) {
        if ("".equals(newTitle)) {clearTagFieldValue(TagField.title);}
        else {tag.setTitle(newTitle);}
        saveTags();
    }

    /**
     * Pomocná metoda pro odstranění hodnoty z tagu, protože knihovna (nejspíš) neumožňuje prostě zavolat např <code>setAlbum("")</code> nebo <code>setAlbum(null)</code>
     * @param field enum, Která hodnota má být odstraněna
     */
    private void clearTagFieldValue(TagField field) {
        ID3v2 newTag = new ID3v23Tag();
        if (field != TagField.artist) newTag.setArtist(tag.getArtist());
        if (field != TagField.year) newTag.setYear(tag.getYear());
        if (field != TagField.album) newTag.setAlbum(tag.getAlbum());
        if (field != TagField.trackNum) newTag.setTrack(tag.getTrack());
        if (field != TagField.title) newTag.setTitle(tag.getTitle());
        tag = newTag;
        file.setId3v2Tag(tag);
        saveTags();
    }

    /**
     * Pomocná metoda pro permanentní uložení tagu
     */
    private void saveTags() {
        try {
            retag();
        } catch (IOException | NotSupportedException ex) {
            throw new RuntimeException("Chyba při ukládání tagu: " + ex);
        } catch (IllegalArgumentException ex) {
            renameFiles();
        }
    }

    private void retag() throws IOException, NotSupportedException {
        file.save(file.getFilename() + ".retag");
        renameFiles();
    }

    private void renameFiles() {
        String filename = file.getFilename();
        File originalFile = new File(filename);
        //File backupFile = new File(filename + ".bak");
        File retaggedFile = new File(filename + ".retag");
        File oldFile = new File(filename + ".old");
        /*
        if (backupFile.exists()) {
            backupFile.delete();
        }
        originalFile.renameTo(backupFile);
        */
        originalFile.renameTo(oldFile);
        retaggedFile.renameTo(originalFile);
        oldFile.delete();
    }

    // #################
    // ### Sortování ###
    // #################

    /**
     * Seřazení podle absolutní cesty k souboru
     * @param other
     * @return
     */
    @Override
    public int compareTo(ITagEditable other) {
        return this.getAbsolutePath().compareTo(other.getAbsolutePath());
    }

}
