package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;


import java.util.HashSet;
import java.util.Set;

// Unit tests for the Cell class
public class CellTest {
    int arbX = 10;
    int arbY = 10;
    Cell cell;

    @BeforeEach
    public void setup() {
        cell = new Cell(arbX, arbY);
    }

    @Test
    public void integrateTest() {
        Set<Cell> soc = new HashSet<>();
        soc.add(stranger1);
        cell.integrate(soc);
        Assertions.assertEquals(0, cell.getNumLiveNeighbours());
        soc.add(n);
        cell.integrate(soc);
        Assertions.assertEquals(1, cell.getNumLiveNeighbours());
        soc.add(ne);
        soc.add(e);
        cell.integrate(soc);
        Assertions.assertEquals(3, cell.getNumLiveNeighbours());
        soc.add(stranger2);
        cell.integrate(soc);
        Assertions.assertEquals(3, cell.getNumLiveNeighbours());
        soc.add(se);
        soc.add(s);
        soc.add(sw);
        soc.add(w);
        soc.add(nw);
        cell.integrate(soc);
        Assertions.assertEquals(8, cell.getNumLiveNeighbours());
    }

    Cell n = new Cell(arbX, arbY - 1);
    Cell ne = new Cell(arbX + 1, arbY - 1);
    Cell e = new Cell(arbX + 1, arbY);
    Cell se = new Cell(arbX + 1, arbY + 1);
    Cell s = new Cell(arbX, arbY + 1);
    Cell sw = new Cell(arbX - 1, arbY + 1);
    Cell w = new Cell(arbX - 1, arbY);
    Cell nw = new Cell(arbX - 1, arbY - 1);
    Cell stranger1 = new Cell(arbX + 2, arbY);
    Cell stranger2 = new Cell(arbX, arbY - 2);
}
