package ui.swinggui;

import logging.Event;
import logging.EventLog;
import persistence.Reader;
import persistence.Writer;
import ui.Game;
import ui.Main;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

public class AppFrame extends JFrame {

    protected final Filer filer;
    private String root;
    private String dataDir = "//Life_data//";
    private String saveDir = "//Games//";
    private static final String SCORE_DATA = "record.json";

    private static final int TICK_INTERVAL = 10;
    protected NavPanel nav;
    protected InfoPanel info;
    protected PlayPanel play;
    protected Gameplay gameplay;
    protected Boolean paused;
    protected Game bestGame;
    protected Game initialGame;
    protected Game game;
    protected Timer tim;
    protected EventLog log;

    // Constructs app frame object and starts app within it
    public AppFrame() {
        super("LifeInteractive");
        String path;
        try {
            path = new File(AppFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        } catch (URISyntaxException e) {
            path = AppFrame.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        }
        root = path.substring(0, path.substring(0, path.length() - 1).lastIndexOf("/"));
        filer = new Filer();
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(false);
        setFocusable(true);
        this.setBackground(new Color(236, 143, 229));
        start();
    }

    // Modifies this
    // Constructs nav with initial app config (triggers call to nav.startMenu())
    // Note: nav menu buttons access and initialize this.game, then make call to this.run()
    private void start() {
        paused = true;
        nav = new NavPanel(this);
        displayNav();
    }

    // Modifies this
    // Runs gameplay app in frame
    public void run() {
        game = initialGame;
        initialGame = new Game(game); // saves duplicate of initial state
        log = EventLog.getInstance();
        log.clear();
        game.getLife().add(log);
        this.addKeyListener(new PlayHandler());
        Reader reader = new Reader(root + dataDir + SCORE_DATA);
        try {
            bestGame = reader.read();
        } catch (Exception e) {
            bestGame = new Game(game);
        }
        paused = false;
        displayGameplay();
        ticker();
    }

    // Modifies this
    // ticks game app, stops when game.isGameOver() == true
    private void ticker() {
        tim = new Timer(TICK_INTERVAL, null);
        ActionListener lis = e -> tickApp();
        tim.addActionListener(lis);
        tim.start();
    }

    // Modifies this
    // checks game and key triggers for frame state changes, and ticks components internally
    private void tickApp() {
        if (game.isGameOver()) {
            endGame();
        } else {
            game.tick();
            play.repaint();
            info.update();
        }
    }

    // Modifies this
    // Runs end-of-game logic
    // Stops ticker, prints log to console, then restarts game sequence or constructs and displays nav;
    // Note: state of this.paused triggers nav.endMenu() in else case;
    protected void endGame() {
        for (Event e : log) {
            System.out.println(e.getDate() + " : " + e.getDescription());
        }
        this.paused = false;
        tim.stop();
        for (KeyListener l : this.getKeyListeners()) {
            this.removeKeyListener(l);
        }
        if (game == null) {
            run();
        } else {
            handleScoreData();
            displayNav();
        }
    }

    // Modifies this, filesystem
    // Handles writing of score data to file
    private void handleScoreData() {
        if (game.getScore() > bestGame.getScore()) {
            bestGame = new Game(game);
            Writer writer = new Writer(root + dataDir + SCORE_DATA);
            try {
                writer.open();
                writer.writeGameState(Main.SURFACE, bestGame);
                writer.close();
            } catch (FileNotFoundException e) {
                System.out.println("Error writing score data");
            }
        }
    }


    // Modifies this
    // Requires this.game != null
    // Stops ticker, clears keyListeners, constructs and displays nav;
    // Note: state of this triggers nav.pauseMenu();
    private void pauseGame() {
        paused = true;
        tim.stop();
        game.suspendEvolution();
        for (KeyListener l : this.getKeyListeners()) {
            this.removeKeyListener(l);
        }
        displayNav();
    }

    // Requires this.game != null;
    // Starts ticker, swaps menu key handler for PlayHandler()
    protected void resumeGame() {
        paused = false;
        game.resumeEvolution();
        for (KeyListener l : this.getKeyListeners()) {
            this.removeKeyListener(l);
        }
        this.addKeyListener(new PlayHandler());
        tim.start();
        displayGameplay();
    }

    // Modifies this
    // Key handler for gameplay
    private class PlayHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            handleKeys(e.getKeyCode());
        }

        private void handleKeys(int keyCode) {
            if (keyCode == KeyEvent.VK_SPACE) {
                pauseGame();
            } else {
                play.handleKeys(keyCode);
            }
        }
    }

    // Modifies this
    // Constructs and displays new navPanel
    protected void displayNav() {
        setVisible(false);
        if (nav != null) {
            this.remove(nav);
        }
        if (gameplay != null) {
            this.remove(gameplay);
        }
        nav = new NavPanel(this);
        this.add(nav);
        pack();
        repaint();
        centerFrame();
        setVisible(true);
    }

    // Modifies this
    // Constructs and displays new Gameplay panel
    protected void displayGameplay() {
        setVisible(false);
        gameplay = new Gameplay();
        this.remove(nav);
        this.add(gameplay);
        pack();
        repaint();
        centerFrame();
        setVisible(true);
    }

    // Subclass to combine Play and Info panels at center of BagGrid
    private class Gameplay extends JPanel {
        // Modifies this
        // Constructs new Info, Play, and Gameplay panels given state of this
        public Gameplay() {
            super();
            this.setLayout(new BorderLayout());
            play = new PlayPanel(game);
            info = new InfoPanel(AppFrame.this);
            this.add(play);
            this.add(info, BorderLayout.NORTH);
        }
    }

    // Subclass to implement file manager display
    protected class Filer extends JPanel implements ActionListener {
        JFileChooser chooser;

        // Constructs file manager panel and displays JFileChooser
        public Filer() {
            super();
            String path = mkDir();
            chooser = new JFileChooser(path);
            chooser.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return (f.isDirectory()) | (f.getName().toLowerCase().endsWith(".json"));
                }

                @Override
                public String getDescription() {
                    return "JSON Files (*.json)";
                }
            });
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Save")) {
                saveDialogue();
            } else if (e.getActionCommand().equals("Load")) {
                loadDialogue();
            }
        }

        // Modifies this, filesystem
        // Dialogue to load game from Json file
        private void loadDialogue() {
            int val = chooser.showOpenDialog(this);
            if (val == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                Reader reader = new Reader(file.getPath());
                try {
                    initialGame = reader.read();
                    run();
                } catch (IOException x) {
                    initialGame = null;
                    start();
                }
            }
        }

        // Modifies this, filesystem
        // Dialogue to save game to Json file
        private void saveDialogue() {
            int val = chooser.showSaveDialog(this);
            if (val == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                Writer writer = new Writer(file.getPath());
                try {
                    writer.open();
                    writer.writeGameState(Main.SURFACE, game);
                    writer.close();
                } catch (FileNotFoundException x) {
                    System.out.println("File not found");
                }
            }
        }
    }

    // Modifies this
    // Sets up local directory structure if not present
    // and returns string for default user save location
    private String mkDir() {
        String path = root;
        File user = new File(path + dataDir);
        if (!user.exists()) {
            if (user.mkdir()) {
                path = path + dataDir;
                if (new File(path + saveDir).mkdir()) {
                    path = path + saveDir;
                    // Complete directory structure
                } else {
                    saveDir = "";
                }
            } else {
                dataDir = "";
            }
        } else {
            path = root + dataDir + saveDir;
        }
        return path;
    }

    // Modifies this
    // Centers frame on screen
    private void centerFrame() {
        setLocation((Main.SCREEN.width - this.getWidth()) / 2, (Main.SCREEN.height - this.getHeight()) / 2);
    }

}
