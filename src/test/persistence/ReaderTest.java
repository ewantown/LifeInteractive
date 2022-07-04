package persistence;
import org.junit.jupiter.api.Test;
import ui.Game;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static ui.Main.SURFACE;

// Tests for reader class
public class ReaderTest {

    @Test
    void ReaderNoFileExceptionTest() {
         Reader reader = new Reader("./data/totallyMadeUpName.json");
         try {
             Game game = reader.read();
             fail("no IO Exception thrown");
         } catch (IOException e) {
             assertTrue(true);
         }
    }

    @Test
    void ReaderBadDataExceptionTest() {
        Reader reader = new Reader("./data//testing/testReadBadData.json");
        try {
            Game game = reader.read();
            fail("no IO Exception thrown");
        } catch (IOException e) {
            assertTrue(true);
        }
    }

    @Test
    void ReaderTypicalGameTest() {
        Reader reader = new Reader("./data/testing/testReadGame.json");
        try {
            Game game = reader.read();
            assertEquals(6, game.getLife().getFootprint().size());
            assertEquals(5, game.getProjectiles().size());
            assertEquals(30, game.getTank().getPosition().getX());
            assertEquals(43, game.getTank().getPosition().getY());
            assertEquals(0, game.getTank().getDirection().getDx());
            assertEquals(-1, game.getTank().getDirection().getDy());
            assertEquals(12, game.getLife().getGen());
        } catch (IOException e) {
            fail("Error reading from file");
        }
    }

    @Test
    void ReaderTypicalFootprintTest() {
        Reader reader = new Reader("./data/testing/testReadFootprint.json");
        try {
            Game game = reader.read();
            assertEquals(6, game.getLife().getFootprint().size());
            assertEquals(0, game.getProjectiles().size());
            assertEquals(SURFACE.getWidth() / 2, game.getTank().getPosition().getX());
            assertEquals(0, game.getTank().getDirection().getDx());
            assertEquals(-1, game.getTank().getDirection().getDy());
            assertEquals(0, game.getLife().getGen());
        } catch (IOException e) {
            fail("Error reading from file");
        }
    }
}
