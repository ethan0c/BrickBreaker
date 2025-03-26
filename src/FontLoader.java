/*******************************************************************
 * CLASS: FontLoader                                              *
 * DESCRIPTION:                                                   *
 * A utility class for loading custom fonts in the game. It       *
 * provides a method to load a font from a file and return it in  *
 * the specified size. If loading fails, a default Arial font is  *
 * returned.                                                      *
 *******************************************************************/
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

public class FontLoader {
    /***************************************************************
     * METHOD: loadFont(String path, float size)                   *
     * DESCRIPTION:                                                *
     * Loads a custom font from the specified file path and returns*
     * it at the given size. If the font cannot be loaded, a       *
     * fallback font (Arial) is returned instead.                  *
     * PARAMETERS:                                                 *
     *    - String path: The file path to the font file.           *
     *    - float size: The desired size for the loaded font.      *
     * RETURN VALUE:                                               *
     *    A `Font` object representing the loaded font, or a       *
     *    default Arial font if loading fails.                     *
     ***************************************************************/
    public static Font loadFont(String path, float size) {
        try {
            // Load the font from the file path
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(path));
            // Derive the font with the specified size
            return font.deriveFont(size);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            // Return a default font if loading fails
            return new Font("Arial", Font.PLAIN, (int) size);
        }
    }
}