package ui.swinggui;

import ui.Game;

import javax.swing.*;
import java.awt.*;

public class InfoPanel extends JPanel {
    private static final String GEN_TXT = "Gen: ";
    private static final String CELL_TXT = "Cells: ";
    private static final String SCORE_TXT = "Score: ";
    private static final String HIGH_SCORE_TXT = "Record: ";
    private static final Dimension LABEL_SIZE = new Dimension(111, 20);

    private final Game game;
    private final JLabel genLabel;
    private final JLabel cellLabel;
    private final JLabel scoreLabel;
    private final JLabel highScoreLabel;
    private long gens;
    private long cellCount;
    private long score;


    // Modifies app
    // Constructs info panel for current game state
    public InfoPanel(AppFrame app) {
        game = app.game;
        cellCount = game.getLife().getFootprint().size();
        gens = game.getLife().getGen();
        score = game.getScore();

        setBackground(new Color(180, 180, 180));

        cellLabel = this.addLabel(CELL_TXT, cellCount);
        scoreLabel = this.addLabel(SCORE_TXT, score);
        highScoreLabel = this.addLabel(HIGH_SCORE_TXT, app.bestGame.getScore());
        genLabel = this.addLabel(GEN_TXT, gens);
    }

    // Modifies this
    // Constructs and adds label displaying text and num
    private JLabel addLabel(String txt, long num) {
        JLabel out = new JLabel(txt + num);
        out.setPreferredSize(LABEL_SIZE);
        add(out);
        add(Box.createHorizontalStrut(9));
        return out;
    }

    // Modifies this
    // Updates fields and info display
    public void update() {
        score = game.getScore();
        cellCount = game.getLife().getFootprint().size();
        gens = game.getLife().getGen();

        scoreLabel.setText(SCORE_TXT + score);
        cellLabel.setText(CELL_TXT + cellCount);
        genLabel.setText(GEN_TXT + gens);

        repaint();
    }
}
