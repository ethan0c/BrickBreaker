/*******************************************************************
 * CLASS: SoundManager                                            *
 * DESCRIPTION:                                                   *
 * A utility class for managing sound effects and background      *
 * music in the game. It provides methods to load, play, loop,    *
 * and stop audio clips.                                          *
 *******************************************************************/
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class SoundManager {

    /***************************************************************
     * METHOD: loadSound(String filePath)                          *
     * DESCRIPTION:                                                *
     * Loads a sound file from the specified file path and returns *
     * a `Clip` object for playback.                               *
     * PARAMETERS:                                                 *
     *    - String filePath: The path to the sound file to load.   *
     * RETURN VALUE:                                               *
     *    A `Clip` object containing the loaded sound, or null if  *
     *    the sound could not be loaded.                           *
     ***************************************************************/
    public Clip loadSound(String filePath) {
        try {
            // Load audio file
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream); // Open the audio stream
            return clip;
        } catch (Exception e) {
            e.printStackTrace(); // Print error details
            return null;
        }
    }

    /***************************************************************
     * METHOD: playSound(Clip clip)                                *
     * DESCRIPTION:                                                *
     * Plays a sound clip from the beginning.                      *
     * PARAMETERS:                                                 *
     *    - Clip clip: The sound clip to play.                     *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    public void playSound(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0); // Rewind to the beginning
            clip.start();             // Start playback
        }
    }

    /***************************************************************
     * METHOD: playLoop(Clip clip)                                 *
     * DESCRIPTION:                                                *
     * Plays a sound clip in a continuous loop.                    *
     * PARAMETERS:                                                 *
     *    - Clip clip: The sound clip to loop.                     *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    public void playLoop(Clip clip) {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop playback continuously
        }
    }

    /***************************************************************
     * METHOD: stopSound(Clip clip)                                *
     * DESCRIPTION:                                                *
     * Stops playback of a sound clip if it is currently playing.  *
     * PARAMETERS:                                                 *
     *    - Clip clip: The sound clip to stop.                     *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    public void stopSound(Clip clip) {
        if (clip != null) {
            clip.stop(); // Stop playback
        }
    }
}