package ui;

import app.Workspace;
import java.util.Scanner;

/**
 * Poskytuje uživatelské rozhraní
 * @author Radek Mocek
 */
public class Main {
    
    private static Scanner sc;
    
    private static Workspace ws;
    
    /**
     * Hlavní UI metoda, která by se měla zapnout, když se zapne program
     * @param args 
     */
    public static void main(String[] args) {
        // Pozdrav
        System.out.println("Dobrý den");
        
        // Inicializace
        sc = new Scanner(System.in);
        ws = new Workspace();
        String input;
        
        // Hlavní smyčka
        while (true) {
            displayWsContent();
            System.out.println("Zadejte příkaz ('help' zobrazí nápovědu):");            
            input = sc.nextLine();
            // Reakce na příkazy            
            // - help
            if (input.equals("help")) {
                displayHelp();
            }
            // - open folder gui
            else if (input.equals("open")) {                
                openFolderGUI();
            }
            // - exit
            else if (input.equals("exit")) {                
                System.exit(0);
            }
            else {
                System.out.println("Neznámý příkaz");
            }
        }        
    }

    // ########################
    // ### Metody pro výpis ###
    // ########################
    
    /**
     * Zobrazí nápovědu
     */
    private static void displayHelp() {
        String help = """
                      * 'open'  – Přidat nové soubory do workspace
                      * 'all'   – Provést akci pro všechny soubory ve workspace
                      * Napsání čísla skladby – provést akci pro jeden konkrétní soubor (první číslo na každém řádku ve workspace)
                      * 'clear' – Odebere soubory z workspace
                      * 'exit'  – Ukončí aplikaci
                      Konec nápovědy""";
        System.out.println(help);
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
    private static void openFolderGUI() {
        String path = FileDialog.selectFolderGUI();
        if (path == null) {
            System.out.println("Výběr složky byl zrušen.");
            return;
        }
        ws.openFolder(path);
    }
    
}
