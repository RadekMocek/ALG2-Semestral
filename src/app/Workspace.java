package app;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import utils.ITagEditable;

/**
 * Třída uchovává mp3 soubory a umožňuje s nimi manipulovat, poskytuje metody pro UI
 * @author Radek Mocek
 */
public class Workspace {

    private List<ITagEditable> audioFiles;

    /**
     * Konstruktor
     */
    public Workspace() {
        audioFiles = new ArrayList<>();
    }

    /**
     * Vrací počet souborů ve workspace (velikost listu)
     * @return int
     */
    public int getNumberOfTracks() {
        return audioFiles.size();
    }

    /**
     * Vrací String, kde jsou pod sebou vypsány všechny soubory, které se aktuálně nachází ve workspace
     * @return String
     */
    public String getPrintableContent() {
        if (audioFiles.isEmpty()) {
            return "Momentálně nejsou načteny žádné skladby.";
        }
        else {
            int maxArtistLen = 0, maxAlbumLen = 0, maxTitleLen = 0;

            for (ITagEditable af : audioFiles) {
                int artistLen = (af.getArtist() == null) ? 4 : af.getArtist().length();
                int albumLen = (af.getAlbum() == null) ? 4 : af.getAlbum().length();
                int titleLen = (af.getTitle() == null) ? 4 : af.getTitle().length();
                if (artistLen > maxArtistLen) maxArtistLen = artistLen;
                if (albumLen > maxAlbumLen) maxAlbumLen = albumLen;
                if (titleLen > maxTitleLen) maxTitleLen = titleLen;
            }

            StringBuilder sb = new StringBuilder(String.format("Momentálně je načteno %d skladeb:", audioFiles.size()));
            for (int i = 1; i <= audioFiles.size(); i++) {
                sb.append(String.format("%n%02d. ", i));
                sb.append(audioFiles.get(i - 1).toStringFormatted(maxArtistLen, maxAlbumLen, maxTitleLen));
            }
            return sb.toString();
        }
    }

    /**
     * Vrací String s informacemi o jednom souboru určeném userIndexem
     * @param userIndex int, číslo skladby ve workspace výpisu (indexujeme tedy od jedničky)
     * @return String
     */
    public String getPrintableFile(int userIndex) {
        return audioFiles.get(userIndex - 1).toString();
    }

    /**
     * V zadané složce najde všechny mp3 soubory a přidá je do workspace
     * @param path Absolutní cesta ke složce (String)
     */
    public void openFolder(String path) {
        File[] listOfFiles = new File(path).listFiles();

        if (listOfFiles == null) {
            System.out.println("Chyba: Neplatná cesta.");
            return;
        }

        List<String> errorFiles = new ArrayList<>();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (fileName.matches("(.*)\\.mp3")) {
                    try {
                        audioFiles.add(new AudioFile(file.getAbsolutePath()));
                    }
                    catch (IOException | UnsupportedTagException | InvalidDataException | IllegalArgumentException ex) {
                        errorFiles.add(fileName);
                    }
                }
            }
        }

        if(!errorFiles.isEmpty()){
            throw new RuntimeException("Chyba: Soubory " + errorFiles.toString() + " nebylo možné načíst.");
        }
    }

    // ####################
    // ### Editace tagů ###
    // ####################

    /**
     * Změní tag interpreta u vybrané skladby
     * @param userIndex int, číslo skladby ve workspace výpisu (indexujeme tedy od jedničky)
     * @param newArtist String, nový název interpreta
     */
    public void changeArtist(int userIndex, String newArtist) {
        audioFiles.get(userIndex - 1).changeArtist(newArtist);
    }

}
