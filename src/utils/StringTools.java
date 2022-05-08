package utils;

/**
 * Pomocná třída pro práci s textovými řetězci
 * @author Radek Mocek
 */
public final class StringTools {

    private StringTools() {}

    /**
     * Vrací, zdali řetězec lze přeparsovat na Integer
     * @param text String, vstupní řetězec
     * @return boolean
     */
    public static boolean tryParseToInt(String text) {
        try {
            Integer.parseInt(text);
            return true;
        }
        catch (NumberFormatException ex) {
            return false;
        }
    }

}
