/*******************************************************************
 * CLASS: BallWelcome                                             *
 * DESCRIPTION:                                                   *
 * Represents a bouncing ball animation for the welcome screen.   *
 * Each ball moves within specified bounds and changes direction  *
 * when colliding with the edges.                                *
 *******************************************************************/
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class BallWelcome {
    private int x, y, xDir, yDir, size;
    private Color color;
    /***************************************************************
     * METHOD: BallWelcome(int x, int y, int xDir, int yDir,       *
     *                     int size, Color color)                 *
     * DESCRIPTION:                                                *
     * Constructor to initialize the ball's position, direction,  *
     * size, and color.                                            *
     * PARAMETERS:                                                 *
     *    - int x: The initial X-coordinate of the ball.           *
     *    - int y: The initial Y-coordinate of the ball.           *
     *    - int xDir: The initial horizontal direction and speed.  *
     *    - int yDir: The initial vertical direction and speed.    *
     *    - int size: The diameter of the ball in pixels.          *
     *    - Color color: The color of the ball.                    *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    public BallWelcome(int x, int y, int xDir, int yDir, int size, Color color) {
        this.x = x;
        this.y = y;
        this.xDir = xDir;
        this.yDir = yDir;
        this.size = size;
        this.color = color;
    }

    /***************************************************************
     * METHOD: move(Rectangle bounds)                              *
     * DESCRIPTION:                                                *
     * Updates the position of the ball based on its direction and *
     * speed. Changes direction if the ball collides with the      *
     * edges of the specified bounds.                              *
     * PARAMETERS:                                                 *
     *    - Rectangle bounds: The bounds within which the ball can *
     *      move.                                                  *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    public void move(Rectangle bounds) {
        // Update position
        x += xDir;
        y += yDir;

        // Reverse direction if ball hits horizontal edges
        if (x <= 0 || x >= bounds.width - size) {
            xDir = -xDir;
        }

        // Reverse direction if ball hits vertical edges
        if (y <= 0 || y >= bounds.height - size) {
            yDir = -yDir;
        }
    }

    /***************************************************************
     * METHOD: draw(Graphics g)                                    *
     * DESCRIPTION:                                                *
     * Renders the ball on the screen using its color and position.*
     * PARAMETERS:                                                 *
     *    - Graphics g: The Graphics object used for rendering.    *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    public void draw(Graphics g) {
        g.setColor(color);           // Set the ball's color
        g.fillOval(x, y, size, size); // Draw the ball as a filled oval
    }

    /***************************************************************
     * METHOD: getBounds()                                         *
     * DESCRIPTION:                                                *
     * Retrieves the bounding rectangle of the ball for collision  *
     * detection.                                                  *
     * PARAMETERS: None.                                           *
     * RETURN VALUE: A Rectangle object representing the ball's    *
     * bounding box.                                               *
     ***************************************************************/
    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
}