package app;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import utils.ITagEditable;
import utils.StringTools;
import utils.TimeTools;

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
        clearWorkspace();

        File[] listOfFiles = new File(path).listFiles();

        if (listOfFiles == null) {
            System.out.println("Chyba: Neplatná cesta.");
            return;
        }

        List<String> errorFiles = new ArrayList<>();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String fileName = file.getName();
                // .mp3
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

    /**
     * Odstraní vybranou skladbu z workspace (nikoliv fyzicky)
     * @param userIndex int, číslo skladby ve workspace výpisu (indexujeme tedy od jedničky)
     */
    public void removeFromWorkspace(int userIndex) {
        audioFiles.remove(userIndex - 1);
    }

    /**
     * Odstraní všechny skladby z workspace (nikoliv fyzicky)
     */
    public void clearWorkspace() {
        audioFiles = new ArrayList<>();
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
    public void changeYear(int userIndex, String newYear) {
        audioFiles.get(userIndex - 1).changeYear(newYear);
    }
    public void changeAlbum(int userIndex, String newAlbum) {
        audioFiles.get(userIndex - 1).changeAlbum(newAlbum);
    }
    public void changeTrackNum(int userIndex, String newTrackNum) {
        audioFiles.get(userIndex - 1).changeTrackNum(newTrackNum);
    }
    public void changeTitle(int userIndex, String newTitle) {
        audioFiles.get(userIndex - 1).changeTitle(newTitle);
    }

    /**
     * Změní tag interpreta u všech skladeb
     * @param newArtist String, nový název interpreta
     */
    public void changeArtistAll(String newArtist) {
        for (ITagEditable audioFile : audioFiles) {
            audioFile.changeArtist(newArtist);
        }
    }
    public void changeYearAll(String newYear) {
        for (ITagEditable audioFile : audioFiles) {
            audioFile.changeYear(newYear);
        }
    }
    public void changeAlbumAll(String newAlbum) {
        for (ITagEditable audioFile : audioFiles) {
            audioFile.changeAlbum(newAlbum);
        }
    }

    // ####################
    // ### Přejmenování ###
    // ####################

    /**
     * Přejmenuje soubor
     * ve vtupním řetězci jsou /i, /y, /a, /n a /t nahrazeny za intepret, rok, album, číslo skladby a název skladby; nepovolené znaky jsou nahrazeny podtržítkem
     * @param userIndex int, číslo skladby ve workspace výpisu (indexujeme tedy od jedničky)
     * @param pattern String
     * @throws IOException
     */
    public void rename(int userIndex, String pattern) throws IOException {
        StringBuilder sb = new StringBuilder();
        ITagEditable audioFile = audioFiles.get(userIndex - 1);
        char[] chars = pattern.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            // /i /y /a /n /t
            if (i != chars.length - 1 && chars[i] == '/') {
                if (chars[i + 1] == 'i') {
                    sb.append(audioFile.getArtist().replaceAll("[/:*?\"<>|\\\\]", "_"));
                    i++;
                }
                else if (chars[i + 1] == 'y') {
                    sb.append(audioFile.getYear().replaceAll("[/:*?\"<>|\\\\]", "_"));
                    i++;
                }
                else if (chars[i + 1] == 'a') {
                    sb.append(audioFile.getAlbum().replaceAll("[/:*?\"<>|\\\\]", "_"));
                    i++;
                }
                else if (chars[i + 1] == 'n') {
                    sb.append(audioFile.getTrackNum().replaceAll("[/:*?\"<>|\\\\]", "_"));
                    i++;
                }
                else if (chars[i + 1] == 't') {
                    sb.append(audioFile.getTitle().replaceAll("[/:*?\"<>|\\\\]", "_"));
                    i++;
                }
                else sb.append("_");
            }
            // Ostatní nepovolené znaky : * ? " < > | \ nahrazeny podtržítkem
            else if ((Character.toString(chars[i])).matches("[:*?\"<>|\\\\]")) {
                sb.append("_");
            }
            else {
                sb.append(chars[i]);
            }
        }

        String newName = sb.toString() + ".mp3";
        Path source = Paths.get(audioFile.getAbsolutePath());
        Files.move(source, source.resolveSibling(newName));
        audioFile.updatePath(newName);
    }

    /**
     * Přejmenuje všechny soubory
     * ve vtupním řetězci jsou /i, /y, /a, /n a /t nahrazeny za intepret, rok, album, číslo skladby a název skladby; nepovolené znaky jsou nahrazeny podtržítkem
     * @param pattern String
     * @throws IOException
     */
    public void renameAll(String pattern) throws IOException {
        for (int i = 0; i < audioFiles.size(); i++) {
            rename(i + 1, pattern);
        }
    }

    // #################
    // ### Sortování ###
    // #################

    /**
     * Seřazení podle názvu souboru
     */
    public void sortByFileName() {
        Collections.sort(audioFiles);
    }

    private static final Comparator<ITagEditable> COMP_BY_ARTIST_YEAR_TRACKNUM = (ITagEditable t1, ITagEditable t2) -> {
        int value = t1.getArtist().compareTo(t2.getArtist());
        if (value == 0) value = t1.getYear().compareTo(t2.getYear());
        if (value == 0) {
            String tn1 = t1.getTrackNum();
            String tn2 = t2.getTrackNum();
            if (StringTools.tryParseToInt(tn1) && StringTools.tryParseToInt(tn2)) value = Integer.compare(Integer.parseInt(tn1), Integer.parseInt(tn2));
            else value = t1.getTrackNum().compareTo(t2.getTrackNum());
        }
        return value;
    };

    /**
     * Seřadí podle interpret - rok - číslo skladby
     */
    public void sortByArtistYearTrackNum() {
        Collections.sort(audioFiles, COMP_BY_ARTIST_YEAR_TRACKNUM);
    }

    /**
     * Seřazení podle roku
     */
    public void sortByYear() {
        Collections.sort(audioFiles, (ITagEditable t1, ITagEditable t2) -> t1.getYear().compareTo(t2.getYear()));
    }

    /**
     * Seřazení podle délky skladby
     */
    public void sortByDuration() {
        Collections.sort(audioFiles, (ITagEditable t1, ITagEditable t2) -> Long.compare(t1.getLengthInSeconds(), t2.getLengthInSeconds()));
    }

    /// ######################
    /// ### Ostatní metody ###
    /// ######################

    /**
     * Generuje soubor description.txt (ve stejné složce, kde se nacházejí soubory z workspace), co řádek to skladba, seřazeno podle aktuálního seřazení workspace,
     * na každém řádku je timestamp skladby relativní ke skladbám předcházejícím (první řádek tedy vždy 0:00; vhodné pro youtube video description), poté následuje pattern
     * @param pattern String, sekvence /i, /y, /a, /n a /t jsou nahrazeny za intepret, rok, album, číslo skladby a název skladby
     * @throws IOException
     */
    public void generateDescription(String pattern) throws IOException {
        String path = audioFiles.get(0).getAbsolutePath();
        File result =  new File(path.substring(0, path.lastIndexOf(File.separator)) + File.separator + "description.txt");
        long totalDuration = 0;
        long fileDuration;
        StringBuilder sb;
        char[] chars = pattern.toCharArray();;
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(result)))) {
            for (ITagEditable audioFile : audioFiles) {
                // Zapsat timestamp
                pw.print(TimeTools.longToString(totalDuration) + " ");
                // Zapsat pattern
                sb = new StringBuilder();
                for (int i = 0; i < chars.length; i++) {
                    if (i != chars.length - 1 && chars[i] == '/') {
                        if (chars[i + 1] == 'i') {
                            sb.append(audioFile.getArtist());
                            i++;
                        }
                        else if (chars[i + 1] == 'y') {
                            sb.append(audioFile.getYear());
                            i++;
                        }
                        else if (chars[i + 1] == 'a') {
                            sb.append(audioFile.getAlbum());
                            i++;
                        }
                        else if (chars[i + 1] == 'n') {
                            sb.append(audioFile.getTrackNum());
                            i++;
                        }
                        else if (chars[i + 1] == 't') {
                            sb.append(audioFile.getTitle());
                            i++;
                        }
                        else sb.append("/");
                    }
                    else {
                        sb.append(chars[i]);
                    }
                }
                pw.println(sb.toString());
                // Inkrementovat timestamp
                fileDuration = audioFile.getLengthInSeconds();
                totalDuration += fileDuration;
            }
        }
    }

}
