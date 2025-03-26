/*******************************************************************
 * Brick Breaker                                                   *
 * PROGRAMMER: Chibudom Onyejesi                                   *
 * COURSE: CS201                                                   *
 * DATE: 12/05/2024                                                *
 * REQUIREMENT: Final Project                                      *
 * DESCRIPTION:                                                    *
 *   The following program uses Graphics, Databases, ArrayLists,   *
 *   and Inheritance to create a visually stimulating game that    *
 *   includes power-ups and sound effects. It is the author's      *
 *   version of the popular game 'Breakout'.                       *
 * COPYRIGHT: This code is copyright (C) 2024 Chibudom Onyejesi    *
 *            and Dean Zeller.                                     *
 * CREDITS: This code was written with the help of ChatGPT.        *
 *******************************************************************/

/******************************************************************
 * CLASS: Main                                                    *
 * DESCRIPTION:                                                   *
 * The entry point for the Brick Breaker game. Sets up the main   *
 * game window, initializes the gameplay logic, and displays the  *
 * welcome screen. Utilizes a `CardLayout` for transitioning      *
 * between the welcome panel and gameplay.                        *
 *******************************************************************/
import javax.swing.*;
import java.awt.*;
public class Main {
    /***************************************************************
     * METHOD: main(String[] args)                                 *
     * DESCRIPTION:                                                *
     * The starting point for the program. Creates the main window *
     * and initializes game components, including the welcome      *
     * screen and gameplay panel.                                  *
     * PARAMETERS:                                                 *
     *    - String[] args: Command-line arguments (not used).      *
     * RETURN VALUE: None.                                         *
     ***************************************************************/
    public static void main(String[] args) {
        // Create the main JFrame object to host the game
        JFrame obj = new JFrame();
        JPanel mainPanel = new JPanel(new CardLayout());

        // Create instances of the gameplay logic and welcome panel
        Gameplay gamePlay = new Gameplay();
        WelcomePanel welcomePanel = new WelcomePanel(mainPanel, gamePlay);

        // Add panels to the mainPanel using CardLayout
        mainPanel.add(welcomePanel, "WelcomePanel");
        mainPanel.add(gamePlay, "Gameplay");

        // Set up the JFrame properties
        obj.setBounds(10, 10, 700, 600); // Window size and position
        obj.setTitle("Brick Breaker by Ethan"); // Window title
        obj.setResizable(false); // Prevent resizing the window
        obj.setVisible(true); // Make the window visible
        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close app on exit
        obj.add(mainPanel); // Add the main panel to the frame
    }
}