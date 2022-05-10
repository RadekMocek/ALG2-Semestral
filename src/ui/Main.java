package ui;

import app.Workspace;
import java.util.Scanner;
import utils.StringTools;

/**
 * Poskytuje uživatelské rozhraní
 * @author Radek Mocek
 */
public class Main {

    private static Scanner sc;

    private static Workspace ws;

    private static int chosenTrack = -1;

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

        // Z testovacích důvodů
        ws.openFolder("D:\\Kod\\ALG2\\Semestral\\testData2");
        // --------------------

        // Hlavní smyčka
        while (true) {
            displayWsContent();
            System.out.println("Zadejte příkaz ('help' zobrazí nápovědu):");
            input = sc.nextLine();
            // Reakce na příkazy
            // - číslo skladby
            if (StringTools.tryParseToInt(input)) {
                int numberOfTracks = ws.getNumberOfTracks();
                if (numberOfTracks == 0) {
                    System.out.println("Momentálně nejsou načteny žádné skladby, není tedy z čeho vybírat.");
                }
                else {
                    int temp = Integer.parseInt(input);
                    if (temp >= 1 && temp <= numberOfTracks) {
                        chosenTrack = Integer.parseInt(input);
                        submenuOneTrack();
                    }
                    else {
                        System.out.println("Skladba s takovým číslem ve workspace není. Zadejte číslo v rozsahu 1 až " + numberOfTracks + ".");
                    }
                }
            }
            // - help
            else if (input.equals("help")) {
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

    // ###############
    // ### Submenu ###
    // ###############

    /**
     * Zobrazí menu s dostupnými akcemi pro jednu skladbu
     */
    private static void submenuOneTrack() {
        String menu = """
                      1. Změnit interpreta
                      2. Změnit rok
                      3. Změnit album
                      4. Změnit číslo stopy
                      5. Změnit skladbu
                      6. Přejmenovat podle tagu
                      7. Změnit tagy podle názvu souboru
                      0. Zpět""";
        String input;
        while (true) {
            System.out.println("Vybraná skladba:");
            System.out.println(ws.getPrintableFile(chosenTrack));
            System.out.println(menu);
            input = sc.nextLine();
            if (StringTools.tryParseToInt(input)) {
                int actionNumber = Integer.parseInt(input);
                if (actionNumber == 0) {
                    break;
                }
                else if (actionNumber == 1) {
                    System.out.println("Zadejte nový název pro interpreta:");
                    input = sc.nextLine();
                    changeArtist(input);
                }
                else {
                    System.out.println("Akce s takovým číslem neexistuje.");
                }
            }
            else {
                System.out.println("Zadejte číslo akce, kterou chcete provést.");
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
                      Stiskněte enter pro ukončení nápovědy""";
        System.out.println(help);
        sc.nextLine();
    }

    /**
     * Vytiskne všechny mp3 soubory, které se aktuálně nachází ve workspace
     */
    private static void displayWsContent() {
        System.out.println(ws.getPrintableContent());
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

    // ####################
    // ### Editace tagů ###
    // ####################

    /**
     * Tag – změna interpreta
     * @param newArtist String, nový název interpreta
     */
    private static void changeArtist(String newArtist) {
        ws.changeArtist(chosenTrack, newArtist);
    }

}
