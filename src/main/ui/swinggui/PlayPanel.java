package ui.swinggui;

import model.Position;
import model.Projectile;
import model.Vehicle;
import ui.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

import static model.Projectile.Type.LIFE_RAY;
import static model.Projectile.Type.SEED;
import static ui.Main.SCALE;
import static ui.Main.SURFACE;

public class PlayPanel extends JPanel {
    private static final Color BACKGROUND = Color.BLACK;
    private static final Color TANK_COL = new Color(236, 2, 236);
    private static final Shape TANK_POS_SHAPE = Shape.SQUARE;
    private static final Color CELL_COL = new Color(77, 246, 41, 255);
    private static final Shape CELL_POS_SHAPE = Shape.POLY;
    private static final Color BULLET_COL = new Color(255, 25, 25);
    private static final Color SEED_COL = new Color(62, 238, 197);
    private static final Shape PROJECTILE_SHAPE = Shape.CIRCLE;

    Game game;

    // Constructs play panel for given game
    public PlayPanel(Game g) {
        game = g;
        setPreferredSize(new Dimension(SURFACE.getWidth() * SCALE, SURFACE.getHeight() * SCALE));
        setBackground(BACKGROUND);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderGame(g);
    }

    // Renders game
    // modifies: g
    // effects: draws game to g
    private void renderGame(Graphics g) {
        renderCells(g);
        renderTank(g);
        renderProjectiles(g);
    }

    // Renders cells
    // modifies: g
    // effects: draws cells to g
    private void renderCells(Graphics g) {
        for (Position p : game.getLife().getFootprint()) {
            draw(g, p, CELL_COL, CELL_POS_SHAPE);
        }
    }

    // Renders tank
    // modifies: g
    // effects: draws tank to g
    private void renderTank(Graphics g) {
        for (Position p : game.getTank().getLocation()) {
            draw(g, p, TANK_COL, TANK_POS_SHAPE);
        }
    }

    // Renders projectiles
    // modifies: g
    // effects: draws projectiles to g
    private void renderProjectiles(Graphics g) {
        for (Projectile p : game.getProjectiles()) {
            Projectile.Type t = p.getType();
            Color c = (t.equals(SEED) | t.equals(LIFE_RAY)) ? SEED_COL : BULLET_COL;
            draw(g, p.getPosition(), c, PROJECTILE_SHAPE);
        }
    }

    // Modifies g
    // Draws instance given shape of given color to graphic at given position
    private void draw(Graphics g, Position p, Color c, Shape s) {
        Color savedCol = g.getColor();
        g.setColor(c);
        int ulx = p.getX() * SCALE;
        int uly = p.getY() * SCALE;
        switch (s) {
            case SQUARE:
                g.fillRect(ulx, uly, SCALE, SCALE);
                break;
            case POLY:
                g.fillRect(ulx + 1, uly + 1, SCALE - 2, SCALE - 2);
                break;
            default: // CIRCLE
                g.fillOval(ulx + 1, uly + 1, SCALE - 2, SCALE - 2);
                break;
        }
        g.setColor(savedCol);
    }

    // Distributes labour
    public void handleKeys(int key) {
        handleDriving(key);
        handleFiring(key);
    }

    // Modifies game
    // Handles user-directed tank-movement
    public void handleDriving(int key) {
        switch (key) {
            case KeyEvent.VK_UP:
                game.directTank(Vehicle.Direction.NORTH);
                break;
            case KeyEvent.VK_RIGHT:
                game.directTank(Vehicle.Direction.EAST);
                break;
            case KeyEvent.VK_DOWN:
                game.directTank(Vehicle.Direction.SOUTH);
                break;
            case KeyEvent.VK_LEFT:
                game.directTank(Vehicle.Direction.WEST);
                break;
        }
    }

    // Modifies game
    // Handles user-directed tank-shooting
    private void handleFiring(int key) {
        switch (key) {
            case KeyEvent.VK_S:
                game.newProjectile(Projectile.Type.DEATH_RAY);
                break;
            case KeyEvent.VK_X:
                game.newProjectile(Projectile.Type.BULLET);
                break;
            case KeyEvent.VK_Z:
                game.newProjectile(Projectile.Type.SEED);
                break;
            case KeyEvent.VK_A:
                game.newProjectile(Projectile.Type.LIFE_RAY);
                break;
        }
    }

    // Enum to govern rendering
    private enum Shape {
        SQUARE, CIRCLE, POLY
    }
}
