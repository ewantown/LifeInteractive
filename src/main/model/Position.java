package model;

import java.util.*;
import java.util.stream.Collectors;

// Representation of positions as pairs of integers
public class Position {
    private final int col;
    private final int row;

    // Constructs position with x(col) and y(row) coordinates
    public Position(int x, int y) {
        col = x;
        row = y;
    }

    // Produces set of (8) positions surrounding the given position
    public Set<Position> neighbourhood() {
        Set<Position> block = new HashSet<>();
        for (int i = this.col - 1; (i <= this.col + 1); i++) {
            for (int j = this.row - 1; (j <= this.row + 1); j++) {
                block.add(new Position(i, j));
            }
        }
        return block.stream().filter(pos -> !(this.equals(pos))).collect(Collectors.toSet());
    }

    public int getX() {
        return this.col;
    }

    public int getY() {
        return this.row;
    }

    // True iff object is this or a position positionally equivalent to this
    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        } else {
            Position pos = (Position) obj;
            return this.col == pos.getX() && this.row == pos.getY();
        }
    }

    // Bookkeeping for Collections (troublesome early bug it was)
    @Override
    public int hashCode() {
        return Objects.hash(col, row);
    }
}