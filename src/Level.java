/*******************************************************************
 * CLASS: Level
 * DESCRIPTION:
 *   The Level class defines the configuration for a particular level
 *   in the Brick Breaker game. A level is characterized by:
 *   - The number of bricks in each row (rowCounts)
 *   - The color of bricks in each row (colors)
 *   - Which rows contain special bricks (specialBricks)
 *
 * RESPONSIBILITIES:
 *   - Store the layout configuration of a level.
 *   - Provide access to the configuration data.
 *
 * USAGE:
 *   Create an instance of this class to define a level configuration.
 *   The Gameplay or Bricks class can then use these configurations to
 *   initialize brick layouts.
 *******************************************************************/

import java.awt.*;

public class Level {
    /**
     * An array indicating how many bricks are in each row of the level.
     * For example, { 13, 11, 9 } would mean the first row has 13 bricks,
     * the second row has 11 bricks, and the third row has 9.
     */
    private int[] rowCounts;

    /**
     * An array of Colors corresponding to each row. The i-th element in
     * this array represents the color of the bricks in row i.
     */
    private Color[] colors;

    /**
     * A boolean array where each element corresponds to a row. A value of true
     * indicates that the corresponding row contains special bricks.
     */
    private boolean[] specialBricks;

    /**
     * CONSTRUCTOR: Level(int[] rowCounts, Color[] colors, boolean[] specialBricks)
     * DESCRIPTION:
     *   Constructs a new Level with the specified configuration.
     *
     * PARAMETERS:
     *   @param rowCounts An array of integers specifying the number of bricks in each row.
     *   @param colors An array of Colors specifying the color of each row of bricks.
     *   @param specialBricks A boolean array indicating which rows contain special bricks.
     *
     * PRECONDITIONS:
     *   - All arrays should be of the same length. The i-th element in each array refers
     *     to the configuration of the i-th row.
     *   - Each color in colors must be non-null.
     */
    public Level(int[] rowCounts, Color[] colors, boolean[] specialBricks) {
        this.rowCounts = rowCounts;
        this.colors = colors;
        this.specialBricks = specialBricks;
    }

    // NOTE: This second constructor is identical in signature to the first one
    // but is empty. If intentional, it should be differentiated (e.g., different parameters).
    // Otherwise, remove this constructor to avoid confusion.
    // public Level(int[] rowCounts, Color[] colors, boolean[] specialBricks) {
    // }

    /**
     * METHOD: getRowCounts()
     * DESCRIPTION:
     *   Retrieves the array of row counts for this level.
     *
     * RETURN VALUE:
     *   An int[] where each element specifies the number of bricks in that row.
     */
    public int[] getRowCounts() {
        return rowCounts;
    }

    /**
     * METHOD: getColors()
     * DESCRIPTION:
     *   Retrieves the array of Colors for each row in this level.
     *
     * RETURN VALUE:
     *   A Color[] where each element specifies the color of the corresponding row of bricks.
     */
    public Color[] getColors() {
        return colors;
    }

    /**
     * METHOD: getSpecialBricks()
     * DESCRIPTION:
     *   Retrieves the boolean array indicating which rows are special.
     *
     * RETURN VALUE:
     *   A boolean[] where each element is true if that row contains special bricks.
     */
    public boolean[] getSpecialBricks() {
        return specialBricks;
    }
}
