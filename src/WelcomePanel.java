/*******************************************************************
 * CLASS: WelcomePanel                                            *
 * DESCRIPTION:                                                   *
 * Represents the welcome screen of the Brick Breaker game. This  *
 * panel displays a welcome message with animated bouncing balls  *
 * and a start button to transition to the gameplay screen. It    *
 * also plays background music during the welcome screen.         *
 *******************************************************************/
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class WelcomePanel extends JPanel {
    // Fields
    private Clip backgroundMusic;          // Background music for the welcome screen
    private SoundManager soundManager;     // Handles audio playback
    private BallWelcome[] balls;           // Array of animated bouncing balls
    private Timer timer;                   // Timer for ball animations
    private Color textColor;               // Color of the welcome text
    private Rectangle textBounds;          // Bounds of the welcome text for collision detection

    /***************************************************************
     * METHOD: WelcomePanel(JPanel mainPanel, Gameplay gamePlay)   *
     * DESCRIPTION:                                                *
     * Constructor to initialize the welcome screen panel. It sets *
     * up the layout, music, animated balls, and start button.     *
     * PARAMETERS:                                                 *
     *    - JPanel mainPanel: The main container holding all       *
     *      game panels.                                           *
     *    - Gameplay gamePlay: The gameplay panel to transition to.*
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    public WelcomePanel(JPanel mainPanel, Gameplay gamePlay) {
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        // Initialize SoundManager and play background music
        soundManager = new SoundManager();
        backgroundMusic = soundManager.loadSound("src/sounds/background.wav");
        new Thread(() -> soundManager.playLoop(backgroundMusic)).start();

        // Load and set the arcade-style font
        Font arcadeFont = FontLoader.loadFont("src/ARCADECLASSIC.ttf", 40f);
        JLabel welcomeLabel = new JLabel("Welcome  to  BRICK BREAKER  by  Ethan", JLabel.CENTER);
        if (arcadeFont != null) {
            welcomeLabel.setFont(arcadeFont);
        } else {
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        }

        textColor = Color.RED;
        welcomeLabel.setForeground(textColor);
        add(welcomeLabel, BorderLayout.CENTER);

        // Set up the start button
        ImageIcon startIcon = new ImageIcon("src/icon.png");
        JButton startButton = new JButton(startIcon);
        startButton.setFont(new Font("Arial", Font.BOLD, 10));
        startButton.setContentAreaFilled(false);
        startButton.setBorderPainted(false);
        startButton.setFocusPainted(false);
        startButton.setMargin(new Insets(0, 0, 0, 0));

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Transition to the gameplay screen
                CardLayout cl = (CardLayout) mainPanel.getLayout();
                cl.show(mainPanel, "Gameplay");
                gamePlay.requestFocusInWindow();
                soundManager.stopSound(backgroundMusic);
                timer.stop();
            }
        });

        add(startButton, BorderLayout.SOUTH);

        // Initialize bouncing balls with random properties
        Random rand = new Random();
        balls = new BallWelcome[3];
        for (int i = 0; i < balls.length; i++) {
            balls[i] = new BallWelcome(
                    rand.nextInt(200),
                    rand.nextInt(200),
                    2 + rand.nextInt(3),
                    2 + rand.nextInt(3),
                    20,
                    new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256))
            );
        }

        // Timer to animate balls and handle text collision detection
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (BallWelcome ball : balls) {
                    ball.move(getBounds());
                    if (textBounds != null && ball.getBounds().intersects(textBounds)) {
                        // Change text color if a ball intersects the text
                        textColor = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
                        welcomeLabel.setForeground(textColor);
                        repaint();
                    }
                }
                repaint();
            }
        });
        timer.start();
    }

    /***************************************************************
     * METHOD: paintComponent(Graphics g)                         *
     * DESCRIPTION:                                                *
     * Renders the welcome screen, including the bouncing balls    *
     * and the dynamic calculation of text bounds.                *
     * PARAMETERS:                                                 *
     *    - Graphics g: The Graphics object for rendering.         *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the animated balls
        for (BallWelcome ball : balls) {
            ball.draw(g);
        }

        // Calculate the bounds of the welcome text
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth("Welcome to BRICK BREAKER by Ethan");
        int textHeight = fm.getHeight();
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() + textHeight) / 2;
        textBounds = new Rectangle(x, y - textHeight, textWidth, textHeight);
    }
}
