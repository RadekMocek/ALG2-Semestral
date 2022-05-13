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

    public void changeArtist(String newArtist);
    public void changeYear(String newYear);
    public void changeAlbum(String newAlbum);
    public void changeTrackNum(String changeTrackNum);
    public void changeTitle(String newTitle);

}
