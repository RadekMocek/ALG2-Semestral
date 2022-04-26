package ui;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

/**
 * Pomocná UI třída pro výběr souborů/složek
 * @author Radek Mocek
 */
public class FileDialog {

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

}
