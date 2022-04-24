package ui;

import app.Workspace;

/**
 * Poskytuje uživatelské rozhraní
 * @author Radek Mocek
 */
public class Main {
    
    private static Workspace ws;
    
    /**
     * Hlavní UI metoda, která by se měla zapnout, když se zapne program
     * @param args 
     */
    public static void main(String[] args) {
        // Pozdrav
        System.out.println("Dobrý den");
        // Inicializace
        ws = new Workspace();
        
        // TEST        
        openFolder();
        displayWsContent();
               
    }

    // ########################
    // ### Metody pro výpis ###
    // ########################
        
    /**
     * Vytiskne menu s funkcemi aplikace
     */
    private static void displayMenu() {
        System.out.println("Menu");
    }
    
    /**
     * Vytiskne všechny mp3 soubory, které se aktuálně nachází ve workspace
     */
    private static void displayWsContent() {
        System.out.println(ws.getContent());
    }
    
    // #################################
    // ### Metody pro import souborů ###
    // #################################
    
    /**
     * Umožní uživateli grafický výběr složky s hudbou, která se "importuje" do workspace
     */
    private static void openFolder() {
        String path = FileDialog.selectFolderGUI();
        if (path == null) {
            System.out.println("Výběr složky byl zrušen.");
        }
        ws.openFolder(path);
    }
    
}
