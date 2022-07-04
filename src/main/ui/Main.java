package ui;

import ui.swinggui.AppFrame;

import java.awt.*;

public class Main {
    public static final int SCALE = 9; // 1 cell == 9x9 pixels
    public static final Dimension SCREEN = Toolkit.getDefaultToolkit().getScreenSize();
    public static final Surface SURFACE = new Surface(
            (int) ((SCREEN.getWidth() / SCALE) * (1.0 / 2.0)), // 1/2 screenWidth
            (int) ((SCREEN.getHeight() / SCALE) * (2.0 / 3.0))); // 2/3 screenHeight

    public static void main(String[] args) {
        AppFrame demo = new AppFrame();
    }
}
