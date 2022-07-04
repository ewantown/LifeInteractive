package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

// import java.util.HashSet;
import java.util.HashSet;
import java.util.Set;

// Unit tests for the position class
public class PositionTest {
    Position pos;

    @BeforeEach
    public void setup() {
        pos = new Position(10, 10);
    }

    @Test
    public void setupTest() {
        Assertions.assertEquals(10, pos.getX());
        Assertions.assertEquals(10, pos.getY());
    }

    @Test
    public void getNeighbourhoodTest() {
        setupTest();
        Set<Position> sop = pos.neighbourhood();
        Assertions.assertEquals(8, sop.size());
        for (Position p : sop) {
            Assertions.assertTrue((p.getX() - 10) <= 1);
            Assertions.assertTrue((p.getX() - 10) >= -1);
            Assertions.assertTrue((p.getY() - 10) <= 1);
            Assertions.assertTrue((p.getY() - 10) >= -1);
            Assertions.assertNotEquals(p, pos);
        }
        Position pos0 = new Position(0, 0);
        Set<Position> nhd0 = pos0.neighbourhood();
        Assertions.assertTrue(nhd0.contains(new Position(-1, -1)));
        Assertions.assertTrue(nhd0.contains(new Position(-1, 0)));
        Assertions.assertTrue(nhd0.contains(new Position(0, -1)));
    }


    @Test
    public void equalsTest() {
        Set<Position> notaPos = new HashSet<>();
        Position p1 = new Position(10, 11);
        Position p2 = new Position(11, 10);
        Position p3 = new Position(11, 11);
        Position p4 = new Position(10, 10);
        Assertions.assertNotEquals(pos, p1);
        Assertions.assertNotEquals(pos, p2);
        Assertions.assertNotEquals(pos, p3);
        Assertions.assertEquals(pos, p4);
    }
}
