package app;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Třída se pokouší o binární vyčtení tagů ze souborů
 * @author Radek Mocek
 */
public class BinaryReader {

    private BinaryReader() {}

    /**
     * Vrací výpis informací získaných z ID3v2.3.x tagu
     * @param file
     * @return String
     * @throws IOException
     */
    public static String getMp3Info(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            byte[] byteBuffer;
            String ascii, asciiID, asciiValue;
            ByteBuffer wrapper;
            int size;
            try {
                // ID3v2/file identifier
                byteBuffer = in.readNBytes(3);
                ascii = new String(byteBuffer);
                if (ascii.equals("ID3")) {
                    // ID3v2 version
                    byteBuffer = in.readNBytes(2);
                    sb.append("\nVersion:\nID3v2.").append(byteBuffer[0]).append(".").append(byteBuffer[1]).append("\n\n");
                    if (byteBuffer[0] == 3) {
                        // ID3v2 flags
                        byteBuffer = in.readNBytes(1);
                        if (byteBuffer[0] == 0) {
                            // ID3v2 size
                            in.readNBytes(4);
                            // ID3v2 frame
                            while (true) {
                                // Frame ID
                                byteBuffer = in.readNBytes(4);
                                wrapper = ByteBuffer.wrap(byteBuffer);
                                size = wrapper.getInt();
                                if (size == 0) {
                                    break;
                                }
                                asciiID = new String(byteBuffer);
                                // Size
                                byteBuffer = in.readNBytes(4);
                                if (byteBuffer.length >= 4 && (byteBuffer[0] < 0 || byteBuffer[1] < 0 || byteBuffer[2] < 0 || byteBuffer[3] < 0)) {
                                    break;
                                }
                                wrapper = ByteBuffer.wrap(byteBuffer);
                                size = wrapper.getInt();
                                // Flags
                                in.readNBytes(2);
                                // Information
                                byteBuffer = in.readNBytes(size);
                                if (asciiID.equals("APIC")) {
                                    sb.append("Soubor obsahuje obal alba\n");
                                }
                                else {
                                    // Identifikátor Stringu, vynecháme první tři znaky
                                    if (byteBuffer.length > 0 && byteBuffer[0] == 1 && byteBuffer[1] == -1 && byteBuffer[2] == -2) {
                                        byteBuffer = Arrays.copyOfRange(byteBuffer, 3, byteBuffer.length - 1);
                                    }
                                    if (byteBuffer.length > 0) {
                                        asciiValue = new String(byteBuffer);
                                        sb.append(translateID3v2FrameID(asciiID)).append(":\n").append(asciiValue).append("\n\n");
                                    }
                                }
                            }
                        }
                        else {
                            sb.append("Tato metoda nepodporuje výpis tohoto souboru, jelikož soubor buď používá tzv. 'Unsynchronisation scheme' nebo 'Extended header' nebo 'Experimental indicator'.");
                        }
                    }
                    else {
                        sb.append("Tato metoda podporuje výpis pouze pro verze ID3v2.3.x.");
                    }
                }
                else {
                    sb.append("Soubor neobsahuje korektní ID3v2 tag.");
                }
            }
            catch (EOFException ex) {
                sb.append("\nNeočekávaně dosažen konec souboru.");
                return sb.toString();
            }
        }

        sb.append("Konec výpisu.");
        return sb.toString();
    }

    /**
     * Pomocná třída pro překlad ID3v2 Frame IDs na jejich význam
     * @param frameID String
     * @return String
     */
    private static String translateID3v2FrameID(String frameID) {
        return switch(frameID) {
            case "TPE1" -> "Lead performer(s)/Soloist(s)";
            case "TPOS" -> "Part of a set";
            case "TRCK" -> "Track number/Position in set";
            case "TYER" -> "Year";
            case "TALB" -> "Album/Movie/Show title";
            case "TCON" -> "Content type";
            case "TIT2" -> "Title/songname/content description";
            case "TXXX" -> "User defined text information frame";
            case "TPE2" -> "Band/orchestra/accompaniment";
            case "TENC" -> "Encoded by";
            case "PRIV" -> "Private frame";
            case "TBPM" -> "BPM (beats per minute)";
            case "TCOP" -> "Copyright message";
            case "USLT" -> "Unsychronized lyric/text transcription";
            case "WXXX" -> "User defined URL link frame";
            case "COMM" -> "Comments";
            case "TPUB" -> "Publisher";
            case "TCOM" -> "Composer";
            case "TSSE" -> "Software/Hardware and settings used for encoding";
            default -> frameID;
        };
    }

}
