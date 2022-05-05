package utils;

/**
 * Rozhraní, které by měla implementovat každá třída reprezentující audio soubor, u kterého je možné měnit tagy
 * @author Radek Mocek
 */
public interface ITagEditable {
    
    public String getArtist();
    
    public String getAlbum();
    
    public String getTitle();
    
    public String toStringFormatted(int artistLen, int albumLen, int titleLen);
    
}
