package ui;

import app.BinaryReader;
import app.Workspace;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

        try {
            // Pokusit se obnovit workspace z minule
            String path = UIFileImporter.loadWorkspaceLocation();
            ws.openFolder(path);
        } catch(FileNotFoundException ex) {
            // Soubor ještě neexistuje – první spuštění programu nebo bylo použito 'clear', netřeba strašit uživatele chybovou hláškou
        } catch (IOException ex) {
            System.out.println("Nepodařilo se obnovit workspace z minulé session.");
            UIFileImporter.clearWorkspaceLocation();
        }

        // Hlavní smyčka
        while (true) {
            System.out.println(ws.getPrintableContent());
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
            // - all
            else if (input.equals("all")) {
                submenuAllTracks();
            }
            // - sort
            else if (input.equals("sort")) {
                submenuSort();
            }
            // - clear
            else if (input.equals("clear")) {
                ws.clearWorkspace();
                UIFileImporter.clearWorkspaceLocation();
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
     * Menu s dostupnými akcemi pro jednu skladbu
     */
    private static void submenuOneTrack() {
        String menu = """
                      1. Změnit interpreta
                      2. Změnit rok
                      3. Změnit album
                      4. Změnit číslo stopy
                      5. Změnit název skladby
                      6. Přejmenovat podle tagu
                      7. Odebrat soubor z workspace
                      8. Vyčíst informace o tagu ze souboru (binárně)
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
                    System.out.println("Zadejte nový název interpreta (ponechte prázdné pro odstranění hodnoty z tagu):");
                    input = sc.nextLine();
                    changeArtist(input);
                }
                else if (actionNumber == 2) {
                    System.out.println("Zadejte nový rok (ponechte prázdné pro odstranění hodnoty z tagu):");
                    input = sc.nextLine();
                    changeYear(input);
                }
                else if (actionNumber == 3) {
                    System.out.println("Zadejte nový název alba (ponechte prázdné pro odstranění hodnoty z tagu):");
                    input = sc.nextLine();
                    changeAlbum(input);
                }
                else if (actionNumber == 4) {
                    System.out.println("Zadejte nové číslo stopy (ponechte prázdné pro odstranění hodnoty z tagu):");
                    input = sc.nextLine();
                    changeTrackNum(input);
                }
                else if (actionNumber == 5) {
                    System.out.println("Zadejte nový název skladby (ponechte prázdné pro odstranění hodnoty z tagu):");
                    input = sc.nextLine();
                    changeTitle(input);
                }
                else if (actionNumber == 6) {
                    System.out.println("Zadejte pattern pro přejmenování (použijte /i /y /a /n /t):");
                    input = sc.nextLine();
                    rename(input);
                    break;
                }
                else if (actionNumber == 7) {
                    ws.removeFromWorkspace(chosenTrack);
                    break;
                }
                else if (actionNumber == 8) {
                    displayBinaryInfo();
                    break;
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

    /**
     * Menu s dostupnými akcemi pro všechny skladby
     */
    private static void submenuAllTracks() {
        String menu = """
                      1. Hromadně změnit interpreta
                      2. Hromadně změnit rok
                      3. Hromadně změnit album
                      4. Hromadně přejmenovat podle tagů
                      5. Vygenerovat popis pro youtube
                      0. Zpět""";
        String input;
        while (true) {
            System.out.println(menu);
            input = sc.nextLine();
            if (StringTools.tryParseToInt(input)) {
                int actionNumber = Integer.parseInt(input);
                if (actionNumber == 0) {
                    break;
                }
                else if (actionNumber == 1) {
                    System.out.println("Zadejte nový název interpreta (ponechte prázdné pro odstranění hodnoty z tagu):");
                    input = sc.nextLine();
                    changeArtistAll(input);
                }
                else if (actionNumber == 2) {
                    System.out.println("Zadejte nový rok (ponechte prázdné pro odstranění hodnoty z tagu):");
                    input = sc.nextLine();
                    changeYearAll(input);
                }
                else if (actionNumber == 3) {
                    System.out.println("Zadejte nový název alba (ponechte prázdné pro odstranění hodnoty z tagu):");
                    input = sc.nextLine();
                    changeAlbumAll(input);
                }
                else if (actionNumber == 4) {
                    System.out.println("Zadejte pattern pro přejmenování (použijte /i /y /a /n /t):");
                    input = sc.nextLine();
                    renameAll(input);
                    break;
                }
                else if (actionNumber == 5) {
                    System.out.println("Zadejte pattern použitý na řádcích výpisu (použijte /i /y /a /n /t):");
                    input = sc.nextLine();
                    generateDescription(input);
                    break;
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

    /**
     * Menu pro řazení skladeb
     */
    private static void submenuSort() {
        String menu = """
                      1. Seřadit podle názvu souboru
                      2. Seřadit podle interpret - rok - číslo stopy
                      3. Seřadit podle roku
                      4. Seřadit podle délky skladby
                      0. Zpět""";
        String input;
        while (true) {
            System.out.println(menu);
            input = sc.nextLine();
            if (StringTools.tryParseToInt(input)) {
                int actionNumber = Integer.parseInt(input);
                if (actionNumber == 0) {
                    break;
                }
                else if (actionNumber == 1) {
                    ws.sortByFileName();
                    break;
                }
                else if (actionNumber == 2) {
                    ws.sortByArtistYearTrackNum();
                    break;
                }
                else if (actionNumber == 3) {
                    ws.sortByYear();
                    break;
                }
                else if (actionNumber == 4) {
                    ws.sortByDuration();
                    break;
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
                      * 'open'  – Vybrat složku se soubory
                      * 'all'   – Provést akci pro všechny soubory ve workspace
                      * 'sort'  – Seřadit soubory ve workspace
                      * Napsání čísla skladby – provést akci pro jeden konkrétní soubor (první číslo na každém řádku ve workspace)
                      * 'clear' – Odebere soubory z workspace
                      * 'exit'  – Ukončí aplikaci
                      Stiskněte enter pro ukončení nápovědy""";
        System.out.println(help);
        sc.nextLine();
    }

    // #################################
    // ### Metody pro import souborů ###
    // #################################

    /**
     * Umožní uživateli grafický výběr složky s hudbou, která se "importuje" do workspace
     */
    private static void openFolderGUI() {
        String path = UIFileImporter.selectFolderGUI();
        if (path == null) {
            System.out.println("Výběr složky byl zrušen.");
            return;
        }
        try {
            ws.openFolder(path);
            UIFileImporter.saveWorkspaceLocation(path);
        }
        catch (RuntimeException ex) {
            System.out.println(ex + ": " + ex.getMessage());
        }
        catch (FileNotFoundException ex) {
            System.out.println(ex + ": " + ex.getMessage());
        }
        catch (IOException ex) {
            System.out.println(ex + ": " + ex.getMessage());
        }
    }

    // ####################
    // ### Editace tagů ###
    // ####################

    /**
     * Tag – změna interpreta pro jednu zvolenou skladbu
     * @param newArtist String, nový název interpreta
     */
    private static void changeArtist(String newArtist) {
        try {
            ws.changeArtist(chosenTrack, newArtist);
        }
        catch(RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }
    private static void changeYear(String newYear) {
        try {
            ws.changeYear(chosenTrack, newYear);
        }
        catch(RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }
    private static void changeAlbum(String newAlbum) {
        try {
            ws.changeAlbum(chosenTrack, newAlbum);
        }
        catch(RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }
    private static void changeTrackNum(String newTrackNum) {
        try {
            ws.changeTrackNum(chosenTrack, newTrackNum);
        }
        catch(RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }
    private static void changeTitle(String newTitle) {
        try {
            ws.changeTitle(chosenTrack, newTitle);
        }
        catch(RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Tag – změna interpreta pro všechny skladby ve workspace
     * @param newArtist String, nový název interpreta
     */
    private static void changeArtistAll(String newArtist) {
        try {
            ws.changeArtistAll(newArtist);
        }
        catch(RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }
    private static void changeYearAll(String newYear) {
        try {
            ws.changeYearAll(newYear);
        }
        catch(RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }
    private static void changeAlbumAll(String newAlbum) {
        try {
            ws.changeAlbumAll(newAlbum);
        }
        catch(RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // ####################
    // ### Přejmenování ###
    // ####################

    /**
     * Přejmenování souboru
     * @param pattern String
     */
    private static void rename(String pattern) {
        try {
            ws.rename(chosenTrack, pattern);
        } catch (IOException ex) {
            System.out.println("Chyba při přejmenování souboru: " + ex);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Přejmenování souborů
     * @param pattern String
     */
    private static void renameAll(String pattern) {
        try {
            ws.renameAll(pattern);
        } catch (IOException ex) {
            System.out.println("Chyba při přejmenování souborů: " + ex);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /// ######################
    /// ### Ostatní metody ###
    /// ######################

    /**
     * Generuje soubor description.txt
     * @param pattern
     */
    private static void generateDescription(String pattern) {
        try {
            ws.generateDescription(pattern);
        } catch (IOException ex) {
            System.out.println("Chyba: Nepodařilo se zapsat soubor: " + ex);
        }
    }

    /**
     * Zobrazí výpis informací získaných z ID3v2.3.x tagu aktuálně vybrané skladby
     */
    private static void displayBinaryInfo() {
        try {
            System.out.println(BinaryReader.getMp3Info(new File(ws.getAbsolutePath(chosenTrack))));
        } catch (IOException ex) {
            System.out.println("Chyba při čtení souboru: " + ex);
        } catch (Exception ex) {
            System.out.println("Nastala neočekávaná chyba: " + ex);
        }
        System.out.println("Stiskněte enter pro návrat do workspace.");
        sc.nextLine();
    }

}
