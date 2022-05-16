package utils;

/**
 * Rozhraní, které by měla implementovat každá třída reprezentující audio soubor, u kterého je možné měnit tagy
 * @author Radek Mocek
 */
public interface ITagEditable extends Comparable<ITagEditable> {

    public void updatePath(String newFileName);

    public String getAbsolutePath();
    public long getLengthInSeconds();

    public String getArtist();
    public String getYear();
    public String getAlbum();
    public String getTrackNum();
    public String getTitle();

    public String toStringFormatted(int artistLen, int albumLen, int titleLen);

    public void changeArtist(String newArtist);
    public void changeYear(String newYear);
    public void changeAlbum(String newAlbum);
    public void changeTrackNum(String changeTrackNum);
    public void changeTitle(String newTitle);

}
