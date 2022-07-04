package ui.swinggui;

import persistence.Reader;
import ui.Game;
import ui.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static ui.Game.DEMO_FOOTPRINT_FN;

public class NavPanel extends JPanel {
    private final InputStream imgFile = NavPanel.class.getResourceAsStream("splash.png");
    private final Color splashBack = new Color(112, 245, 143, 255);
    private final AppFrame app;

    // Modifies this, app;
    // Constructs nav panel, with config determined by state of app;
    public NavPanel(AppFrame app) {
        super();
        this.app = app;
        this.setLayout(new OverlayLayout(this));
        if (app.paused) {
            if (app.game == null) {
                startMenu();
            } else {
                pauseMenu();
            }
        } else {
            endMenu();
        }
        splash();
    }

    // Modifies this
    // Builds startMenu
    private void startMenu() {
        JPanel buttons = new ButtonPanel(4);
        buttons.add(quickStartButton());
        buttons.add(filerButton("Load"));
        buttons.add(drawButton());
        buttons.add(surpriseButton());
        this.add(buttons);
    }

    // Modifies this
    // Builds pauseMenu
    private void pauseMenu() {
        ButtonPanel buttons = new ButtonPanel(4);
        buttons.add(resumeButton());
        buttons.add(restartButton());
        buttons.add(filerButton("Save"));
        buttons.add(quitButton());
        this.add(buttons);
        app.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    app.resumeGame();
                }
            }
        });
    }

    // Modifies this
    // Builds endMenu
    private void endMenu() {
        JPanel buttons = new ButtonPanel(4);
        buttons.add(quickStartButton());
        buttons.add(filerButton("Load"));
        buttons.add(drawButton());
        buttons.add(surpriseButton());
        this.add(buttons);
    }

    // Constructs and returns button with associated listener
    private JButton quickStartButton() {
        Button quickStart = new Button("Start");
        ActionListener initDemo = e -> {
            app.initialGame = new Game(Main.SURFACE, DEMO_FOOTPRINT_FN); // saves initial game
            app.game = new Game(Main.SURFACE, DEMO_FOOTPRINT_FN); // initializes demo game
            app.run();
        };
        quickStart.addActionListener(initDemo);
        return quickStart;
    }

    // Constructs and returns button with associated listener
    private JButton drawButton() {
        Button drawStart = new Button("Seed");
        ActionListener initDraw = e -> {
            app.initialGame = new Game(Main.SURFACE, DEMO_FOOTPRINT_FN); // saves initial game
            app.game = new Game(Main.SURFACE, DEMO_FOOTPRINT_FN); // initializes demo game
            app.initialGame.suspendEvolution();
            app.game.suspendEvolution();
            app.run();
        };
        drawStart.addActionListener(initDraw);
        return drawStart;
    }

    // Constructs and returns button with associated listener
    private JButton restartButton() {
        Button restartButton = new Button("Restart");
        ActionListener restart = e -> {
            app.game = null;
            app.endGame();
        };
        restartButton.addActionListener(restart);
        return restartButton;
    }

    // Constructs and returns button with associated listener
    private JButton quitButton() {
        Button quitButton = new Button("Quit");
        ActionListener quit = e -> app.endGame();
        quitButton.addActionListener(quit);
        return quitButton;
    }

    // Constructs and returns button with associated listener
    private JButton resumeButton() {
        Button resumeButton = new Button("Resume");
        ActionListener resume = e -> app.resumeGame();
        resumeButton.addActionListener(resume);
        return resumeButton;
    }

    // Given action command string, constructs and returns file command button with associated listener
    private JButton filerButton(String cmd) {
        Button button = new Button(cmd);
        button.setActionCommand(cmd);
        button.addActionListener(app.filer);
        return button;
    }

    // Constructs and returns button with associated listener;
    private JButton surpriseButton() {
        Button surpriseButton = new Button("Surprise");
        ActionListener initGenRec = e -> genRec();
        surpriseButton.addActionListener(initGenRec);
        return surpriseButton;
    }

    // TODO: write a generative recursive footprint function to pass to the Game constructor
    private void genRec() {
        app.initialGame = new Game(Main.SURFACE, Game.GEN_REC); // saves initial game
        app.game = new Game(Main.SURFACE, Game.GEN_REC); // initializes demo game
        app.run();
    }

    // Adds panel displaying splash image against colored background, over which button panel is to be laid
    private void splash() {
        try {
            BufferedImage i = ImageIO.read(imgFile);
            JPanel splashPanel = new JPanel();
            splashPanel.setBackground(splashBack);
            JLabel label = new JLabel(new ImageIcon(i));
            label.setAlignmentX(0.5f);
            label.setAlignmentY(0.5f);
            splashPanel.add(label);
            //splashPanel.setForeground(new Color(223, 105, 214)); // ad hoc at .jar fix
            this.add(splashPanel);
        } catch (IOException e) {
            System.out.println("Error retrieving background image");
            this.setPreferredSize(new Dimension(300, 200));
        }
    }

    // Subclass for customization of default JButton preferences
    private static class Button extends JButton {
        public Button(String s) {
            super(s);
            this.setSize(new Dimension(20, 10));
            this.setAlignmentX(0.5f);
            this.setOpaque(false);
        }
    }

    // Subclass for custom button display
    private static class ButtonPanel extends JPanel {
        // Constructs a panel with n vertically arranged button slots
        public ButtonPanel(int n) {
            super();
            this.setMaximumSize(new Dimension(130, 100));
            GridLayout layout = new GridLayout(n, 0, 1, 1);
            this.setLayout(layout);
            this.setAlignmentX(0.5f);
            this.setAlignmentY(0.5f);
            this.setOpaque(false);
            this.setVisible(true);
        }
    }
}
