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
            if (input.substring(0, 4).equals("help")) {
                displayHelp();
            }
            // - open folder gui
            else if (input.substring(0, 4).equals("open")) {                
                openFolderGUI();
            }
            // - exit
            else if (input.substring(0, 4).equals("exit")) {                
                System.exit(0);
            }
        }        
    }

    // ########################
    // ### Metody pro výpis ###
    // ########################
        
    /**
     * Vytiskne menu s funkcemi aplikace
     */
//    private static void displayMenu() {
//        System.out.println("Menu");
//    }
    
    /**
     * Zobrazí nápovědu
     */
    private static void displayHelp() {
        System.out.println("nápověda");
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
