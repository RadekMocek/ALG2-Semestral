package ui;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

/**
 * Pomocná UI třída pro výběr přidání souborů do workspace
 * @author Radek Mocek
 */
public class UIFileImporter {

    private UIFileImporter() {}

    // ##############################
    // ### Otevřít nový workspace ###
    // ##############################

    /**
     * Zobrazí uživateli dialog s výzvou výběru složky, vrací String s absolutní cestou této složky nebo <code>null</code>, pokud uživatel zruší výběr
     * @return String s absolutní cestou nebo <code>null</code>, pokud uživatel zruší výběr
     */
    static String selectFolderGUI() {
        // Instance JFileChooser, která umožní uživateli jednoduše vybrat soubor (adresář)
        JFileChooser fc = new JFileChooser() {
            // Po použití Scanner.nextLine se focus nastaví na konzoli a následně zavolané dialogy se nezobrazí (alespoň v netbeans ne)
            // tímto kódem se dá problém obejít (https://stackoverflow.com/questions/28141885/jfilechooser-showsavedialog-not-showing-up)
            @Override
            protected JDialog createDialog(Component parent) throws HeadlessException {
                JDialog jDialog = super.createDialog(parent);
                jDialog.setAlwaysOnTop(true);
                return jDialog;
            }
        };
        // Počáteční adresář chooseru je ten, kde se nachází aplikace
        fc.setCurrentDirectory(new File("."));
        // Nastavení hlavičky okna
        fc.setDialogTitle("Vyberte složku");
        // Chceme vybírat celý adresář
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        // Zobrazit dialog, první parametr rodiče můžeme nastavit na null, druhý parametr je název potvrzovacího tlačítka
        fc.showDialog(null, "Otevřít");
        // getCurrentDirectory vrací adresář nadřazený tomu, co uživatel vybral; použijeme tedy getSelectedFile
        File chosenFolder = fc.getSelectedFile();
        if (chosenFolder == null) {
            return null;
        }
        return (fc.getSelectedFile().toString());
    }

    // ###############################
    // ### Uložit/načíst workspace ###
    // ###############################

    private final static String WORKSPACE_LOCATION_PATH = "data" + File.separator + "state.audiows";

    static void saveWorkspaceLocation(String path) throws FileNotFoundException, IOException {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(WORKSPACE_LOCATION_PATH, false))) {
            out.writeUTF(path);
        }
    }

    static String loadWorkspaceLocation() throws FileNotFoundException, IOException {
        String rtrn;
        try (DataInputStream in = new DataInputStream(new FileInputStream(WORKSPACE_LOCATION_PATH))) {
            rtrn = in.readUTF();
        }
        return rtrn;
    }

    static void clearWorkspaceLocation() {
        File state = new File(WORKSPACE_LOCATION_PATH);
        state.delete();
    }

}
