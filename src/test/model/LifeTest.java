package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashSet;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Unit tests for the Life class
public class LifeTest {
    private static final int ARB_X = 100;
    private static final int ARB_Y = 100;
    private static final int CTR_X = ARB_X / 2;
    private static final int CTR_Y = ARB_Y / 2;

    private Life lf;
    private Set<Position> footprint;

    private final Set<Position> TEST_INIT_FOOTPRINT = Stream.of(
            new Position(CTR_X, CTR_Y), // center
            new Position(CTR_X, CTR_Y - 1), // n
            new Position(CTR_X + 1, CTR_Y - 1), // ne
            new Position(CTR_X + 1, CTR_Y), // e
            new Position(CTR_X + 1, CTR_Y + 1), // se
            new Position(CTR_X, CTR_Y + 1), // s
            new Position(CTR_X - 1, CTR_Y + 1), // sw
            new Position(CTR_X - 1, CTR_Y), // w
            new Position(CTR_X - 1, CTR_Y - 1)  // nw
    ).collect(Collectors.toSet());

    @BeforeEach
    public void setup() {
        lf = new Life(TEST_INIT_FOOTPRINT);
        footprint = lf.getFootprint();
    }

    @Test
    public void setupTest() {
        Assertions.assertEquals(0, lf.getGen());
        Assertions.assertEquals(footprint, TEST_INIT_FOOTPRINT);
        Assertions.assertEquals(9, footprint.size());
        Assertions.assertEquals(0, lf.getLifeRay().size());
    }

    @Test
    public void evolveTest() {
        setupTest();

        lf.evolve();
        Assertions.assertEquals(1, lf.getGen());
        Assertions.assertEquals(8, lf.getFootprint().size());
        Assertions.assertFalse(lf.getFootprint().contains(new Position(CTR_X, CTR_Y)));
        Assertions.assertFalse(lf.getFootprint().contains(new Position(CTR_X, CTR_Y - 1)));
        Assertions.assertFalse(lf.getFootprint().contains(new Position(CTR_X, CTR_Y + 1)));
        Assertions.assertFalse(lf.getFootprint().contains(new Position(CTR_X - 1, CTR_Y)));
        Assertions.assertFalse(lf.getFootprint().contains(new Position(CTR_X + 1, CTR_Y)));
        Assertions.assertTrue(lf.getFootprint().contains(new Position(CTR_X, CTR_Y - 2)));
        Assertions.assertTrue(lf.getFootprint().contains(new Position(CTR_X, CTR_Y + 2)));
        Assertions.assertTrue(lf.getFootprint().contains(new Position(CTR_X - 2, CTR_Y)));
        Assertions.assertTrue(lf.getFootprint().contains(new Position(CTR_X + 2, CTR_Y)));

        lf.evolve();

        lf.updateRays(Stream.of(new Position(0, 0)).collect(Collectors.toSet()), new HashSet<>());

        lf.evolve();
        Assertions.assertEquals(3, lf.getGen());
        Assertions.assertEquals(12, lf.getFootprint().size());
        Assertions.assertEquals(0, lf.getFootprint().stream()
                .filter(p -> !(p.getX() == CTR_X - 2 || p.getX() == CTR_X + 2)
                        && !(p.getY() == CTR_Y - 2 || p.getY() == CTR_Y + 2))
                .count());

        lf.updateRays(Stream.of(new Position(CTR_X - 2, CTR_Y)).collect(Collectors.toSet()), new HashSet<>());

        lf.evolve();
    }

    @Test
    public void addCellTest() {
        setupTest();
        Position p = new Position(CTR_X + 2, CTR_Y + 2);
        Assertions.assertFalse(lf.getFootprint().contains(p));
        lf.addCell(p);
        Assertions.assertTrue(lf.getFootprint().contains(p));
    }

    @Test
    public void removeCellTest() {
        setupTest();
        Position p = new Position(CTR_X, CTR_Y);
        Assertions.assertTrue(lf.getFootprint().contains(p));
        lf.removeCell(p);
        Assertions.assertFalse(lf.getFootprint().contains(p));
    }

    @Test
    public void updateRaysTest() {
        setupTest();
        Position l1 = new Position(CTR_X + 1, CTR_Y + 1);
        Position l2 = new Position(CTR_X - 2, CTR_Y - 2);
        Position d1 = new Position(CTR_X - 1, CTR_Y - 1);
        Position d2 = new Position(CTR_X + 2, CTR_Y + 2);

        Set<Position> liferay = new HashSet<>();
        liferay.add(l1);
        liferay.add(l2);
        Set<Position> deathray = new HashSet<>();
        deathray.add(d1);
        deathray.add(d2);

        lf.updateRays(deathray, liferay);

        Assertions.assertTrue(lf.getLifeRay().contains(l1));
        Assertions.assertTrue(lf.getLifeRay().contains(l2));
        Assertions.assertEquals(2, lf.getLifeRay().size());

        Assertions.assertTrue(lf.getDeathRay().contains(d1));
        Assertions.assertTrue(lf.getDeathRay().contains(d2));
        Assertions.assertEquals(2, lf.getDeathRay().size());

    }
}

