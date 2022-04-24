package app;

import java.io.File;
import java.util.ArrayList;
import utils.ITagEditable;

/**
 * Třída uchovává mp3 soubory a umožňuje s nimi manipulovat, poskytuje metody pro UI
 * @author Radek Mocek
 */
public class Workspace {

    private ArrayList<ITagEditable> audioFiles;
    
    /**
     * Konstruktor
     */
    public Workspace() {
        audioFiles = new ArrayList<>();
    }
    
    /**
     * Vrací String, kde jsou pod sebou vypsány všechny soubory, které se aktuálně nachází ve workspace
     * @return String
     */
    public String getContent() {
        if (audioFiles.isEmpty()) {
            return "Momentálně nejsou načteny žádné skladby.";
        }
        else {
            StringBuilder sb = new StringBuilder(String.format("Momentálně je načteno %d skladeb:", audioFiles.size()));
            for (ITagEditable audioFile : audioFiles) {
                sb.append("\n");
                sb.append(audioFile);
            }
            return sb.toString();
        }
    }
    
    /**
     * V zadané složce najde všechny mp3 soubory a přidá je do workspace
     * @param path Absolutní cesta ke složce (String)
     */
    public void openFolder(String path) {        
        File[] listOfFiles = new File(path).listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                String fileName = file.getName();                
                if (fileName.matches("(.*)\\.mp3")) {
                    audioFiles.add(new AudioFile(file.getAbsolutePath()));
                }
            }
        }
    }
    
}
