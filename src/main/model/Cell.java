package model;

import java.util.Set;
import java.util.stream.Collectors;

// Representation of a single cell in Conway's Game of Life
public class Cell {

    private final Position position;
    private int numLiveNeighbours;

    // Constructs cell w/ position for which x, y values given
    public Cell(int col, int row) {
        this.position = new Position(col, row);
        this.numLiveNeighbours = 0;
    }

    // Modifies this
    // Updates numLiveNeighbours with count of neighbours of this in soc
    public void integrate(Set<Cell> soc) {
        Set<Position> socFootprint = soc.stream().map(Cell::getPosition).collect(Collectors.toSet());
        this.numLiveNeighbours = (int) this.getPosition().neighbourhood().stream()
                .filter(socFootprint::contains)
                .count();
    }

    // Getters
    public Position getPosition() {
        return position;
    }

    public int getNumLiveNeighbours() {
        return numLiveNeighbours;
    }

}


