package persistence;

import model.Position;
import model.Projectile;
import org.junit.jupiter.api.Test;
import ui.Game;
import ui.Surface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static ui.Main.SURFACE;

public class WriterTest {
    private final Function<Surface, Set<Position>> foo = s ->
            Stream.of(new Position(20, 20)).collect(Collectors.toSet());
    private Game game;
    private Writer writer;


    @Test
    void writeGameFileNotFoundExceptionTest() {
        try {
            game = new Game(SURFACE, foo);
            writer = new Writer("./data/is\0NotAFile.json");
            writer.open();
            fail("no FileNotFoundException thrown");
        } catch (FileNotFoundException e) {
            assertTrue(true);
        }
    }


        @Test
    void writeGameTest() {
        try {
            game = new Game(SURFACE, foo);
            game.newProjectile(Projectile.Type.BULLET);
            game.getLife().addCell(new Position(game.getTank().getCanonPos().getX(),
                    game.getTank().getCanonPos().getY() + Projectile.BULLET_SPEED + 1));
            game.tick();
            writer = new Writer("./data/testing/testWriteGame.json");
            writer.open();
            writer.writeGameState(SURFACE, game);
            writer.close();

            Reader reader = new Reader("./data/testing/testWriteGame.json");
            game = reader.read();
            assertEquals(1, game.getProjectiles().size());
            assertEquals(2, game.getLife().getFootprint().size());
            assertEquals(0, game.getLife().getGen());
        } catch (FileNotFoundException e) {
            fail("FileNotFoundException should not be thrown");
        } catch (IOException e) {
            fail("IOException should not be thrown");
        }
    }

    @Test
    void writeFootprintFileNotFoundExceptionTest() {
        try {
            game = new Game(SURFACE, foo);
            writer = new Writer("./data/is\0NotAFile.json");
            writer.open();
            fail("no FileNotFoundException thrown");
        } catch (FileNotFoundException e) {
            assertTrue(true);
        }
    }

/*
    @Test
    void writeFootprintTest() {
        try {
            game = new Game(SURFACE, foo);
            game.newProjectile(Projectile.Type.BULLET);
            game.getLife().addCell(new Position(game.getTank().getCanonPos().getX(),
                    game.getTank().getCanonPos().getY() + Projectile.BULLET_SPEED + 1));
            game.tick();
            writer = new Writer("./data/testing/testWriteFootprint.json");
            writer.open();
            writer.writeFootprint(game);
            writer.close();

            Reader reader = new Reader("./data/testing/testWriteFootprint.json");
            game = reader.read();
            assertEquals(0, game.getProjectiles().size());
            assertEquals(2, game.getLife().getFootprint().size());
            assertEquals(0, game.getLife().getGen());
        } catch (FileNotFoundException e) {
            fail("FileNotFoundException should not be thrown");
        } catch (IOException e) {
            fail("IOException should not be thrown");
        }
    }

 */
}
