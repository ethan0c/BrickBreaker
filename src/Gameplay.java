/*******************************************************************
 * CLASS: Gameplay                                                *
 * DESCRIPTION:                                                   *
 * This class implements the core gameplay mechanics for a brick  *
 * breaker game. It extends JPanel and uses event listeners for   *
 * handling user input and game updates. The class includes       *
 * features like paddle control, ball movement, collision         *
 * detection, power-ups, scoring, and sound effects.              *
 *  *******************************************************************/
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.sound.sampled.Clip;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Gameplay extends JPanel implements KeyListener, ActionListener {
    // Fields
    private SoundManager soundManager;
    private Clip brickHitSound;
    private Clip paddleHitSound;
    private Clip gameOverSound;
    private Clip winnerSound;
    private Clip powerUpSound;
    private Clip backgroundMusic;
    private Clip specialBrickHitSound;
    private Clip wallHitSound;
    private Font arcadeFont;

    private final int PANEL_WIDTH = 700;    // Panel width
    private final int PANEL_HEIGHT = 600;   // Panel height
    private Bricks bricks;                  // Bricks in the game
    private boolean play = false;           // Game state (playing or paused)
    private int score = 0;                  // Player score
    private int PaddleWidth = 100;          // Paddle width in pixels
    private int lives = 1;                  // Player lives
    private List<Ball> balls = new ArrayList<>(); // List of active balls
    private ArrayList<Integer> highScore = new ArrayList<>(); // High scores
    private Timer timer;                    // Timer for game updates
    private int delay = 5;                  // Delay in milliseconds
    private int playerX = 310;              // Paddle's X position

    private double ballSpeedX = -400;       // Ball horizontal speed (px/s)
    private double ballSpeedY = -500;       // Ball vertical speed (px/s)

    private List<PowerUp> activePowerUps = new ArrayList<>(); // Active power-ups
    private boolean gameWon = false;          // Game won flag

    /***************************************************************
     * METHOD: Gameplay()                                          *
     * DESCRIPTION:                                                *
     * Constructor for the Gameplay class. Initializes sound       *
     * effects, fonts, bricks, ball parameters, and starts the     *
     * game timer.                                                 *
     * PARAMETERS: None.                                           *
     * RETURN VALUE: None.                                         *
     ***************************************************************/// Game won flag
    public Gameplay() {
        soundManager = new SoundManager();
        brickHitSound = soundManager.loadSound("src/sounds/brickHitSound.wav");
        paddleHitSound = soundManager.loadSound("src/sounds/paddleHitSound.wav");
        specialBrickHitSound = soundManager.loadSound("src/sounds/specialBrickHitSound.wav");
        gameOverSound = soundManager.loadSound("src/sounds/gameOverSound.wav");
        winnerSound = soundManager.loadSound("src/sounds/winnerSound.wav");
        powerUpSound = soundManager.loadSound("src/sounds/powerUpSound.wav");
        wallHitSound = soundManager.loadSound("src/sounds/wallHitSound.wav");

        arcadeFont = FontLoader.loadFont("src/ARCADECLASSIC.ttf", 16f);

        bricks = new Bricks();
        highScore.add(0);

        // Ball starts near the center
        double ballStartX = PANEL_WIDTH / 2.0;
        double ballStartY = PANEL_HEIGHT / 2.0;
        balls.add(new Ball(ballStartX, ballStartY, ballSpeedX, ballSpeedY));

        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        timer = new Timer(delay, this);
        timer.start();
    }
    /***************************************************************
     * METHOD: paintComponent(Graphics g)                         *
     * DESCRIPTION:                                                *
     * Paints the game components, including the paddle, bricks,   *
     * balls, power-ups, score, and game messages (e.g., Game Over *
     * or You Win).                                                *
     * PARAMETERS:                                                 *
     *    - Graphics g: The Graphics object used for rendering.    *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Use high-quality rendering
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Background
        Color darkBlue = new Color(0, 0, 139);
        g2.setColor(darkBlue);
        g2.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        // Border
        g2.setColor(Color.YELLOW);
        g2.setStroke(new BasicStroke(5));
        // Adjust border to panel size
        g2.drawRect(1, 1, 686, 562);

        // Draw bricks
        if (bricks != null) {
            for (Brick brick : bricks) {
                brick.draw(g2);
            }
        }

        // Draw paddle
        g2.setColor(Color.WHITE);
        g2.fillRect(playerX, PANEL_HEIGHT - 50, PaddleWidth, 10);

        // Draw balls
        for (Ball ball : balls) {
            ball.draw(g2);
        }

        // Draw power-ups
        drawPowerUps(g2);

        // Font fallback
        Font scoreFont = (arcadeFont != null) ? arcadeFont.deriveFont(13f) : new Font("Arial", Font.PLAIN, 13);
        g2.setFont(scoreFont);
        g2.setColor(Color.WHITE);
        g2.drawString("Score    " + score, PANEL_WIDTH - 120, 25);
        g2.drawString("Lives    " + lives, PANEL_WIDTH - 120, 45);
        g2.drawString("High Score    " + Collections.max(highScore), PANEL_WIDTH - 150, PANEL_HEIGHT - 100);

        // Game Over or Win Conditions
        if (gameWon) {
            drawWinnerMessage(g2);
        } else if (!play && lives <= 0)
        {
            if(!highScore.contains(score))
            {
                highScore.add(score);
            }
            drawGameOverMessage(g2);
        }


        g2.dispose();
    }
    /***************************************************************
     * METHOD: gameWon()                                           *
     * DESCRIPTION:                                                *
     * Stops the game and marks it as won. Plays the win sound     *
     * effect and updates the high score.                          *
     * PARAMETERS: None.                                           *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    public void gameWon() {
        play = false;    // Stop the game loop
        gameWon = true;  // Set the gameWon flag
        highScore.add(score); // Add the final score to the high scores
        if (winnerSound != null) {
           soundManager.playSound(winnerSound);
        }
        repaint(); // Update the screen to show winner message
    }
    /***************************************************************
     * METHOD: dropPowerUp(int x, int y)                           *
     * DESCRIPTION:                                                *
     * Spawns a new power-up at the given location.                *
     * PARAMETERS:                                                 *
     *    - int x: X-coordinate of the power-up spawn location.    *
     *    - int y: Y-coordinate of the power-up spawn location.    *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    private void drawPowerUps(Graphics2D g2) {
        for (PowerUp powerUp : activePowerUps) {
            powerUp.draw(g2);
        }
    }
    /*******************************************************************
     * METHOD: checkWinCondition()                                    *
     * DESCRIPTION:                                                   *
     * Checks if all bricks have been cleared from the game. If no    *
     * bricks remain, the `gameWon()` method is called to end the game *
     * with a win state.                                              *
     * PARAMETERS: None.                                              *
     * RETURN VALUE: None.                                            *
     *******************************************************************/
    private void checkWinCondition() {
        if (!bricks.iterator().hasNext()) {
            gameWon();
        }
    }
    /*******************************************************************
     * METHOD: drawGameOverMessage(Graphics g2)                       *
     * DESCRIPTION:                                                   *
     * Displays the "Game Over" message on the screen along with the  *
     * player's score and instructions to restart the game.           *
     * PARAMETERS:                                                    *
     *    - Graphics g2: The Graphics2D object used for rendering.    *
     * RETURN VALUE: None.                                            *
     *******************************************************************/
    private void drawGameOverMessage(Graphics g2) {
        g2.setColor(Color.RED);
        g2.setFont(arcadeFont.deriveFont(40f));
        g2.drawString("Game Over", 240, 230);
        g2.drawString("Score    " + score, 240, 300);
        g2.drawString("Press ENTER to restart", 120, 400);
    }
    /*******************************************************************
     * METHOD: drawWinnerMessage(Graphics g2)                         *
     * DESCRIPTION:                                                   *
     * Displays the "You Win" message on the screen along with the    *
     * player's score and instructions to restart the game.           *
     * PARAMETERS:                                                    *
     *    - Graphics g2: The Graphics2D object used for rendering.    *
     * RETURN VALUE: None.                                            *
     *******************************************************************/
    private void drawWinnerMessage(Graphics g2) {
        g2.setColor(Color.GREEN);
        g2.setFont(arcadeFont.deriveFont(40f));
        g2.drawString("YOU WIN   Score   " + score, 150, 300);
        g2.drawString("Press ENTER to restart", 190, 400);
    }
    /***************************************************************
     * METHOD: actionPerformed(ActionEvent e)                     *
     * DESCRIPTION:                                                *
     * Updates the game state, including ball movement, collision  *
     * detection, and power-up effects, at each timer tick.        *
     * PARAMETERS:                                                 *
     *    - ActionEvent e: The event triggered by the timer.       *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if (play) {
            // Delta time in seconds
            double delta = delay / 1000.0;

            Iterator<Ball> ballIterator = balls.iterator();
            while (ballIterator.hasNext()) {
                Ball ball = ballIterator.next();
                ball.move(delta);

                // Check wall collisions
                // Left & Right walls
                if (ball.getX() < 0) {
                    ball.setX(0);
                    ball.invertXDir();
                    playSoundIfNotNull(wallHitSound);
                } else if (ball.getX() + ball.getSize() > PANEL_WIDTH) {
                    ball.setX(PANEL_WIDTH - ball.getSize());
                    ball.invertXDir();
                    playSoundIfNotNull(wallHitSound);
                }

                // Top wall
                if (ball.getY() < 0) {
                    ball.setY(0);
                    ball.invertYDir();
                    playSoundIfNotNull(wallHitSound);
                }

                // Paddle collision
                Rectangle paddleRect = new Rectangle(playerX, PANEL_HEIGHT - 50, PaddleWidth, 10);
                if (ball.getRect().intersects(paddleRect)) {
                    // Variable bounce angle
                    int paddleCenter = playerX + PaddleWidth / 2;
                    int ballCenter = (int) (ball.getX() + ball.getSize() / 2);
                    int hitPos = ballCenter - paddleCenter;

                    // Adjust x velocity based on hit position
                    // More offset -> more horizontal velocity
                    double newXVel = hitPos * 4; // tune factor as desired
                    double newYVel = -Math.abs(ball.getYVel());

                    ball.setXVel(newXVel);
                    ball.setYVel(newYVel);

                    // Ensure the ball is placed right above the paddle
                    ball.setY(PANEL_HEIGHT - 50 - ball.getSize());
                    playSoundIfNotNull(paddleHitSound);
                }

                // Brick collision
                Iterator<Brick> brickIterator = bricks.iterator();
                boolean brickHit = false;
                while (brickIterator.hasNext()) {
                    Brick brick = brickIterator.next();
                    if (brick.getBoundingRectangle().intersects(ball.getRect())) {
                        brickIterator.remove();
                        score += 5;
                        // Invert vertical direction on brick hit
                        ball.invertYDir();
                        brickHit = true;

                        // Check if special brick
                        if (brick.isSpecial()) {
                            playSoundIfNotNull(specialBrickHitSound);
                            dropPowerUp(brick.getbrickX(), brick.getbrickY());
                        } else {
                            playSoundIfNotNull(brickHitSound);
                        }
                        break;
                    }
                }

                // Bottom boundary (ball lost)
                if (ball.getY() > PANEL_HEIGHT - ball.getSize()) {
                    ballIterator.remove();
                    if (balls.isEmpty()) {
                        if (lives > 1) {
                            lives--;
                            spawnNewBall();
                        } else {
                            lives = 0;
                            if (!highScore.contains(score)) {
                                highScore.add(score);
                            }
                            play = false;
                            playSoundIfNotNull(gameOverSound);
                        }
                    }
                }
            }

            checkWinCondition();
            // Update power-ups
            updatePowerUps(new Rectangle(playerX, PANEL_HEIGHT - 50, PaddleWidth, 10));

            repaint();
        }
    }
    /***************************************************************
     * METHOD: spawnNewBall()                                      *
     * DESCRIPTION:                                                *
     * Adds a new ball to the game with the default speed and size *
     * PARAMETERS: None.                                           *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    private void spawnNewBall() {
        // Spawn ball from paddle center
        double startX = playerX + PaddleWidth / 2.0;
        double startY = PANEL_HEIGHT - 60;
        // Initial speed as before
        balls.add(new Ball(startX, startY, -400, -500));
    }
    /***************************************************************
     * METHOD: resetBall()                                         *
     * DESCRIPTION:                                                *
     * Resets the position of the ball and resumes play.           *
     * PARAMETERS: None.                                           *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    private void resetBall() {
        balls.clear();
        spawnNewBall();
        play = true;
    }
    /***************************************************************
     * METHOD: moveLeft()                                          *
     * DESCRIPTION:                                                *
     * Moves the paddle to the left, if within bounds.             *
     * PARAMETERS: None.                                           *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    public void moveLeft() {
        if (playerX > 10) {
            playerX -= 20;
        }
    }
    /***************************************************************
     * METHOD: moveRight()                                         *
     * DESCRIPTION:                                                *
     * Moves the paddle to the right, if within bounds.            *
     * PARAMETERS: None.                                           *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    public void moveRight() {
        if (playerX < PANEL_WIDTH - PaddleWidth - 10) {
            playerX += 20;
        }
    }
    /***************************************************************
     * METHOD: restartGame()                                       *
     * DESCRIPTION:                                                *
     * Resets the game state, including score, lives, paddle size, *
     * and bricks, and spawns a new ball to start the game.        *
     * PARAMETERS: None.                                           *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    private void restartGame() {
        highScore.add(score);
        playerX = (PANEL_WIDTH - PaddleWidth) / 2;
        score = 0;
        lives = 1;
        PaddleWidth = 100;
        bricks = new Bricks();
        balls.clear();
        spawnNewBall();
        activePowerUps.clear();
        gameWon = false;
        play = true;
        repaint();
    }
    /***************************************************************
     * METHOD: playSoundIfNotNull(Clip clip)                       *
     * DESCRIPTION:                                                *
     * Plays a sound effect if the clip is not null.               *
     * PARAMETERS:                                                 *
     *    - Clip clip: The sound clip to play.                     *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    private void playSoundIfNotNull(Clip clip) {
        if (clip != null) soundManager.playSound(clip);
    }
    /*******************************************************************
     * METHOD: keyPressed(KeyEvent e)                                 *
     * DESCRIPTION:                                                   *
     * Handles user keyboard input for controlling the paddle and     *
     * restarting the game. The left and right arrow keys move the    *
     * paddle, and the Enter key restarts the game when it is not     *
     * currently in play.                                             *
     * PARAMETERS:                                                    *
     *    - KeyEvent e: The event object containing information       *
     *      about the key that was pressed.                           *
     * RETURN VALUE: None.                                            *
     *******************************************************************/
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            moveLeft();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            moveRight();
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER && !play) {
            restartGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) { /* Not used */ }
    @Override
    public void keyTyped(KeyEvent e) { /* Not used */ }

    /*******************************************************************
     * CLASS: Brick                                                   *
     * DESCRIPTION:                                                   *
     * Represents a single brick in the game, including its position, *
     * size, color, and special attributes. The class provides        *
     * methods to render the brick and retrieve its properties for    *
     * collision detection and game logic.                            *
     *******************************************************************/
    class Brick {
        public static final int WIDTH = 40;
        public static final int HEIGHT = 20;
        private int x;
        private int y;
        private Color color;
        private boolean isSpecial;
        private Rectangle boundingRectangle; // Rectangle for collision detection
        /***************************************************************
         * METHOD: Brick(int x, int y, Color color, boolean isSpecial) *
         * DESCRIPTION:                                                *
         * Constructor to initialize a brick with its position, color, *
         * and special status.                                         *
         * PARAMETERS:                                                 *
         *    - int x: The X-coordinate of the brick.                  *
         *    - int y: The Y-coordinate of the brick.                  *
         *    - Color color: The color of the brick.                   *
         *    - boolean isSpecial: True if the brick has special       *
         *      properties, otherwise false.                           *
         * RETURN VALUE: None.                                         *
         ***************************************************************/
        public Brick(int x, int y, Color color, boolean isSpecial) {
            this.x = x;
            this.y = y;
            this.color = color;
            this.isSpecial = isSpecial;
            boundingRectangle = new Rectangle(x, y, WIDTH, HEIGHT);
        }

        /***************************************************************
         * METHOD: draw(Graphics2D g2)                                 *
         * DESCRIPTION:                                                *
         * Draws the brick on the screen with its assigned color.      *
         * PARAMETERS:                                                 *
         *    - Graphics2D g2: The Graphics2D object for rendering.    *
         * RETURN VALUE: None.                                         *
         ***************************************************************/
        public void draw(Graphics2D g2) {
            g2.setColor(color);
            g2.fill3DRect(x, y, WIDTH, HEIGHT, true);
        }

        /***************************************************************
         * METHOD: isSpecial()                                         *
         * DESCRIPTION:                                                *
         * Checks if the brick is special (e.g., triggers power-ups).  *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: True if the brick is special, otherwise false.*
         ***************************************************************/
        public boolean isSpecial() {
            return isSpecial;
        }

        /***************************************************************
         * METHOD: getbrickX()                                         *
         * DESCRIPTION:                                                *
         * Retrieves the X-coordinate of the brick.                    *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: The X-coordinate of the brick as an integer.  *
         ***************************************************************/
        public int getbrickX() {
            return x;
        }

        /***************************************************************
         * METHOD: getbrickY()                                         *
         * DESCRIPTION:                                                *
         * Retrieves the Y-coordinate of the brick.                    *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: The Y-coordinate of the brick as an integer.  *
         ***************************************************************/
        public int getbrickY() {
            return y;
        }

        /***************************************************************
         * METHOD: getBoundingRectangle()                              *
         * DESCRIPTION:                                                *
         * Retrieves the bounding rectangle of the brick for collision *
         * detection purposes.                                         *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: A Rectangle object representing the brick's   *
         * bounding box.                                               *
         ***************************************************************/
        public Rectangle getBoundingRectangle() {
            return boundingRectangle;
        }
    }
    /*******************************************************************
     * CLASS: Bricks                                                  *
     * DESCRIPTION:                                                   *
     * Represents a collection of bricks in the game. This class      *
     * manages the creation, layout, and iteration of all bricks. It  *
     * supports collision detection and dynamic removal of bricks.    *
     * Implements the `Iterable` interface to allow iteration through *
     * all bricks.                                                    *
     *******************************************************************/
    class Bricks implements Iterable<Brick> {
        // Constants
        public static final int X_SPACING = 10;         // Horizontal spacing between bricks
        public static final int Y_SPACING = X_SPACING; // Vertical spacing between bricks
        public static final int[] ROW_COUNTS = { 13, 11, 9, 7, 6, 5, 3, 1 }; // Bricks per row
        private static final float MIN_SAT = 0.8f;     // Minimum saturation for brick colors
        private static final Color SPECIAL_COLOR = Color.RED; // Color for special bricks

        // Fields
        private List<Brick> brickList;
        private Random random = new Random();

        /***************************************************************
         * METHOD: Bricks()                                            *
         * DESCRIPTION:                                                *
         * Constructor to initialize the collection of bricks with a   *
         * specific layout and attributes.                             *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: None.                                         *
         ***************************************************************/
        public Bricks() {
            init(); // Initialize the bricks layout
        }
        /***************************************************************
         * METHOD: init()                                              *
         * DESCRIPTION:                                                *
         * Creates and positions bricks in rows with spacing. Each     *
         * brick is assigned a random color, and one special brick is  *
         * placed randomly per row if conditions are met.              *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: None.                                         *
         ***************************************************************/
        private void init() {
            brickList = new ArrayList<>();
            int startY = 50;
            int startX = 20;
            boolean specialBrickPlaced = false;

            for (int row = 0; row < ROW_COUNTS.length; row++) {
                int bricksInRow = ROW_COUNTS[row];
                for (int col = 0; col < bricksInRow; col++) {
                    int x = startX + col * (Brick.WIDTH + X_SPACING);
                    int y = startY + row * (Brick.HEIGHT + Y_SPACING);
                    boolean isSpecial = false;
                    Color color;
                    if (!specialBrickPlaced && random.nextInt(bricksInRow) == col) {
                        // Assign a special brick
                        color = SPECIAL_COLOR;
                        isSpecial = true;
                        specialBrickPlaced = true;
                    } else {
                        // Assign a random color
                        color = new Color(random.nextFloat(), MIN_SAT, random.nextFloat());
                    }
                    // Add the brick to the list
                    brickList.add(new Brick(x, y, color, isSpecial));
                }
                specialBrickPlaced = false;
            }
        }
        /***************************************************************
         * METHOD: iterator()                                          *
         * DESCRIPTION:                                                *
         * Returns an iterator for the collection of bricks, enabling  *
         * iteration through all bricks.                               *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: An `Iterator<Brick>` for the brick collection.*
         ***************************************************************/
        @Override
        public Iterator<Brick> iterator() {
            return brickList.iterator();
        }
    }
    /*******************************************************************
     * CLASS: Ball                                                    *
     * DESCRIPTION:                                                   *
     * Represents a ball in the game, including its position, size,   *
     * velocity, and methods to handle movement and collisions.       *
     *******************************************************************/
    class Ball {
        private double x;
        private double y;
        private double xVel;
        private double yVel;
        private int size = 14;
        /***************************************************************
         * METHOD: Ball(double x, double y, double xVel, double yVel)  *
         * DESCRIPTION:                                                *
         * Constructor to initialize the ball with its starting        *
         * position and velocity.                                      *
         * PARAMETERS:                                                 *
         *    - double x: The initial X-coordinate of the ball.        *
         *    - double y: The initial Y-coordinate of the ball.        *
         *    - double xVel: The initial horizontal velocity of the    *
         *      ball.                                                  *
         *    - double yVel: The initial vertical velocity of the ball.*
         * RETURN VALUE: None.                                         *
         ***************************************************************/
        public Ball(double x, double y, double xVel, double yVel) {

            this.x = x;
            this.y = y;
            this.xVel = xVel;
            this.yVel = yVel;
        }

        /***************************************************************
         * METHOD: setSize(int size)                                   *
         * DESCRIPTION:                                                *
         * Sets the size (diameter) of the ball.                       *
         * PARAMETERS:                                                 *
         *    - int size: The new size of the ball in pixels.          *
         * RETURN VALUE: None.                                         *
         ***************************************************************/
        public void setSize(int size) {
            this.size = size;
        }

        /***************************************************************
         * METHOD: getSize()                                           *
         * DESCRIPTION:                                                *
         * Retrieves the size (diameter) of the ball.                  *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: The size of the ball as an integer.           *
         ***************************************************************/
        public int getSize() {
            return size;
        }

        /***************************************************************
         * METHOD: move(double delta)                                  *
         * DESCRIPTION:                                                *
         * Updates the position of the ball based on its velocity and  *
         * the elapsed time (delta).                                   *
         * PARAMETERS:                                                 *
         *    - double delta: The elapsed time in seconds.             *
         * RETURN VALUE: None.                                         *
         ***************************************************************/
        public void move(double delta) {
            x += xVel * delta;
            y += yVel * delta;
        }

        /***************************************************************
         * METHOD: invertXDir()                                        *
         * DESCRIPTION:                                                *
         * Reverses the horizontal direction of the ball.              *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: None.                                         *
         ***************************************************************/
        public void invertXDir() {
            xVel = -xVel;
        }

        /***************************************************************
         * METHOD: invertYDir()                                        *
         * DESCRIPTION:                                                *
         * Reverses the vertical direction of the ball.                *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: None.                                         *
         ***************************************************************/
        public void invertYDir() {
            yVel = -yVel;
        }

        /***************************************************************
         * METHOD: getRect()                                           *
         * DESCRIPTION:                                                *
         * Retrieves the bounding rectangle of the ball for collision  *
         * detection purposes.                                         *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: A Rectangle object representing the ball's    *
         * bounding box.                                               *
         ***************************************************************/
        public Rectangle getRect() {
            return new Rectangle((int) x, (int) y, size, size);
        }

        /***************************************************************
         * METHOD: draw(Graphics2D g2)                                 *
         * DESCRIPTION:                                                *
         * Draws the ball on the screen using a gradient fill for      *
         * visual effect.                                              *
         * PARAMETERS:                                                 *
         *    - Graphics2D g2: The Graphics2D object for rendering.    *
         * RETURN VALUE: None.                                         *
         ***************************************************************/
        public void draw(Graphics2D g2) {
            GradientPaint gradient = new GradientPaint(
                    (float) x, (float) y, Color.RED,
                    (float) (x + size), (float) (y + size), Color.DARK_GRAY,
                    true);
            g2.setPaint(gradient);
            g2.fillOval((int) x, (int) y, size, size);
            g2.setPaint(null);
        }

        /***************************************************************
         * METHOD: getX()                                              *
         * DESCRIPTION:                                                *
         * Retrieves the X-coordinate of the ball.                     *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: The X-coordinate of the ball as a double.     *
         ***************************************************************/
        public double getX() {
            return x;
        }

        /***************************************************************
         * METHOD: getY()                                              *
         * DESCRIPTION:                                                *
         * Retrieves the Y-coordinate of the ball.                     *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: The Y-coordinate of the ball as a double.     *
         ***************************************************************/
        public double getY() {
            return y;
        }

        /***************************************************************
         * METHOD: setX(double x)                                      *
         * DESCRIPTION:                                                *
         * Sets the X-coordinate of the ball.                          *
         * PARAMETERS:                                                 *
         *    - double x: The new X-coordinate of the ball.            *
         * RETURN VALUE: None.                                         *
         ***************************************************************/
        public void setX(double x) {
            this.x = x;
        }

        /***************************************************************
         * METHOD: setY(double y)                                      *
         * DESCRIPTION:                                                *
         * Sets the Y-coordinate of the ball.                          *
         * PARAMETERS:                                                 *
         *    - double y: The new Y-coordinate of the ball.            *
         * RETURN VALUE: None.                                         *
         ***************************************************************/
        public void setY(double y) {
            this.y = y;
        }

        /***************************************************************
         * METHOD: getXVel()                                           *
         * DESCRIPTION:                                                *
         * Retrieves the horizontal velocity of the ball.              *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: The horizontal velocity of the ball as a      *
         * double.                                                     *
         ***************************************************************/
        public double getXVel() {
            return xVel;
        }

        /***************************************************************
         * METHOD: getYVel()                                           *
         * DESCRIPTION:                                                *
         * Retrieves the vertical velocity of the ball.                *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: The vertical velocity of the ball as a double.*
         ***************************************************************/
        public double getYVel() {
            return yVel;
        }

        /***************************************************************
         * METHOD: setXVel(double xVel)                                *
         * DESCRIPTION:                                                *
         * Sets the horizontal velocity of the ball.                   *
         * PARAMETERS:                                                 *
         *    - double xVel: The new horizontal velocity.              *
         * RETURN VALUE: None.                                         *
         ***************************************************************/
        public void setXVel(double xVel) {
            this.xVel = xVel;
        }

        /***************************************************************
         * METHOD: setYVel(double yVel)                                *
         * DESCRIPTION:                                                *
         * Sets the vertical velocity of the ball.                     *
         * PARAMETERS:                                                 *
         *    - double yVel: The new vertical velocity.                *
         * RETURN VALUE: None.                                         *
         ***************************************************************/
        public void setYVel(double yVel) {
            this.yVel = yVel;
        }
    }
    /*******************************************************************
     * METHOD: dropPowerUp(int x, int y)                              *
     * DESCRIPTION:                                                   *
     * Spawns a power-up at the specified location on the screen.     *
     * The type of power-up is selected randomly from the available   *
     * options in the `PowerUp.PowerUpType` enum.                     *
     * PARAMETERS:                                                    *
     *    - int x: The X-coordinate of the spawn location.            *
     *    - int y: The Y-coordinate of the spawn location.            *
     * RETURN VALUE: None.                                            *
     *******************************************************************/
    private void dropPowerUp(int x, int y) {
        PowerUp.PowerUpType[] powerUpTypes = PowerUp.PowerUpType.values();
        PowerUp.PowerUpType randomType = powerUpTypes[new Random().nextInt(powerUpTypes.length)];
        PowerUp powerUp = new PowerUp(x + Brick.WIDTH / 2 - 10, y + Brick.HEIGHT, randomType);
        activePowerUps.add(powerUp);
    }
    /*******************************************************************
     * METHOD: applyPowerUpEffect(PowerUp.PowerUpType type)           *
     * DESCRIPTION:                                                   *
     * Applies the effect of a specific power-up type to the game.    *
     * Depending on the power-up type, the method modifies ball size, *
     * paddle size, ball speed, or spawns additional balls.           *
     * PARAMETERS:                                                    *
     *    - PowerUp.PowerUpType type: The type of power-up effect to  *
     *      apply.                                                    *
     * RETURN VALUE: None.                                            *
     *******************************************************************/
    private void applyPowerUpEffect(PowerUp.PowerUpType type) {
        switch (type) {
            case BIGGER_BALL -> {
                for (Ball ball : balls) {
                    ball.setSize(ball.getSize() * 3);
                }
                System.out.println("Ball size increased!");
            }
            case LONGER_PADDLE -> {
                PaddleWidth += 30;
                System.out.println("Paddle Length Increased!");
            }
            case MANY_BALLS -> {
                spawnExtraBalls(3);
                System.out.println("3 Extra Balls Added!");
            }
            case DOUBLE_SPEED -> {
                for (Ball b : balls) {
                    b.setXVel(b.getXVel() * 2);
                    b.setYVel(b.getYVel() * 2);
                }
                System.out.println("Ball Speed Doubled!");
            }
        }
    }
    /*******************************************************************
     * METHOD: spawnExtraBalls(int count)                             *
     * DESCRIPTION:                                                   *
     * Spawns a specified number of additional balls in the game,     *
     * originating from the paddle's center. Each ball is given a     *
     * random horizontal direction and a predefined vertical speed.   *
     * PARAMETERS:                                                    *
     *    - int count: The number of balls to spawn.                  *
     * RETURN VALUE: None.                                            *
     *******************************************************************/
    private void spawnExtraBalls(int count) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            // Calculate the starting position for the new ball
            double startX = playerX + PaddleWidth / 2.0; // Center of the paddle
            double startY = PANEL_HEIGHT - 60;          // Slightly above the paddle
            // Determine a random horizontal velocity direction
            double xVel = rand.nextBoolean() ? -200 : 200;

            // Set a constant vertical velocity
            double yVel = -300;

            // Create a new Ball object and add it to the list of balls
            balls.add(new Ball(startX, startY, xVel, yVel));
        }
    }
    /*******************************************************************
     * CLASS: PowerUp                                                 *
     * DESCRIPTION:                                                   *
     * Represents a power-up in the game that falls from destroyed    *
     * bricks and interacts with the paddle to activate specific      *
     * effects. Includes properties such as type, position, size, and *
     * color, as well as methods for movement and collision detection.*
     *******************************************************************/
    class PowerUp {
        public enum PowerUpType {
            // Enum : Provides predefined types of power-ups, each associated with a unique effect in the game.
            BIGGER_BALL, LONGER_PADDLE, MANY_BALLS, DOUBLE_SPEED
        }

        private int x;
        private int y;
        private int width = 20;
        private int height = 20;
        private Color color = Color.YELLOW;
        private PowerUpType type;

        /***************************************************************
         * METHOD: PowerUp(int x, int y, PowerUpType type)            *
         * DESCRIPTION:                                                *
         * Constructor to initialize a power-up with its position,     *
         * type, and corresponding color.                              *
         * PARAMETERS:                                                 *
         *    - int x: The initial X-coordinate of the power-up.       *
         *    - int y: The initial Y-coordinate of the power-up.       *
         *    - PowerUpType type: The type of the power-up.            *
         * RETURN VALUE: None.                                         *
         ***************************************************************/
        public PowerUp(int x, int y, PowerUpType type) {
            this.x = x;
            this.y = y;
            this.type = type;

            // Assign color based on power-up type
            switch (type) {
                case BIGGER_BALL -> color = Color.BLACK;
                case LONGER_PADDLE -> color = Color.BLUE;
                case MANY_BALLS -> color = Color.ORANGE;
                case DOUBLE_SPEED -> color = Color.RED;
            }
        }

        /***************************************************************
         * METHOD: draw(Graphics2D g2)                                 *
         * DESCRIPTION:                                                *
         * Draws the power-up on the screen with its associated color. *
         * PARAMETERS:                                                 *
         *    - Graphics2D g2: The Graphics2D object for rendering.    *
         * RETURN VALUE: None.                                         *
         ***************************************************************/
        public void draw(Graphics2D g2) {
            g2.setColor(color);
            g2.fillOval(x, y, width, height);
        }

        /***************************************************************
         * METHOD: update()                                            *
         * DESCRIPTION:                                                *
         * Updates the position of the power-up, causing it to move    *
         * downward on the screen.                                     *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: None.                                         *
         ***************************************************************/
        public void update() {
            y += 2; // Moves the power-up downward at a constant rate
        }

        /***************************************************************
         * METHOD: intersects(Rectangle paddle)                       *
         * DESCRIPTION:                                                *
         * Checks if the power-up intersects with the paddle.          *
         * PARAMETERS:                                                 *
         *    - Rectangle paddle: The bounding rectangle of the paddle.*
         * RETURN VALUE: True if the power-up intersects the paddle;   *
         * false otherwise.                                            *
         ***************************************************************/
        public boolean intersects(Rectangle paddle) {
            return new Rectangle(x, y, width, height).intersects(paddle);
        }

        /***************************************************************
         * METHOD: getType()                                           *
         * DESCRIPTION:                                                *
         * Retrieves the type of the power-up.                         *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: The type of the power-up as a `PowerUpType`.  *
         ***************************************************************/
        public PowerUpType getType() {
            return type;
        }

        /***************************************************************
         * METHOD: getY()                                              *
         * DESCRIPTION:                                                *
         * Retrieves the current Y-coordinate of the power-up.         *
         * PARAMETERS: None.                                           *
         * RETURN VALUE: The Y-coordinate of the power-up as an integer.*
         ***************************************************************/
        public int getY() {
            return y;
        }
    }
    /*******************************************************************
     * METHOD: updatePowerUps(Rectangle paddle)                       *
     * DESCRIPTION:                                                   *
     * Updates the state of all active power-ups. This method checks  *
     * if any power-up intersects with the paddle to apply its effect *
     * or if it falls out of bounds to remove it.                     *
     * PARAMETERS:                                                    *
     *    - Rectangle paddle: The bounding rectangle of the paddle.   *
     * RETURN VALUE: None.                                            *
     *******************************************************************/
    private void updatePowerUps(Rectangle paddle) {
        Iterator<PowerUp> iterator = activePowerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp powerUp = iterator.next();
            powerUp.update();// Move the power-up down the screen
            // If the power-up intersects the paddle, apply its effect
            if (powerUp.intersects(paddle)) {
                applyPowerUpEffect(powerUp.getType());
                playSoundIfNotNull(powerUpSound);
                iterator.remove();// Remove the power-up after activation
            } else if (powerUp.getY() > PANEL_HEIGHT) {
                // Remove the power-up if it falls out of the screen
                iterator.remove();
            }
        }
    }
}
