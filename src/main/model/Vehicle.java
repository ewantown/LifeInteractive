package model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Abstract representation of a moving object
public abstract class Vehicle {
    private Position position;
    private List<Position> location;
    private Direction direction;
    private int velocity;

    public Vehicle(Position pos) {
        position = pos;
        location = new ArrayList<>();
        direction = Direction.NONE;
        velocity = 0;
    }

    private Position nextPosition() {
        return new Position(
                position.getX() + (velocity * direction.getDx()),
                position.getY() + (velocity * direction.getDy()));
    }

    public List<Position> nextLocation() {
        return this.location.stream()
                .map(pos -> new Position(
                        pos.getX() + (this.velocity * this.direction.getDx()),
                        pos.getY() + (this.velocity * this.direction.getDy())))
                .collect(Collectors.toList());
    }

    public void move() {
        this.position = this.nextPosition();
        this.location = this.nextLocation();
    }

    // Getters and setters

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position p) {
        this.position = p;
    }

    public List<Position> getLocation() {
        return this.location;
    }

    public void setLocation(List<Position> lop) {
        this.location = lop;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction dir) {
        this.direction = dir;
    }

    public int getVelocity() {
        return this.velocity;
    }

    public void setVelocity(int vel) {
        this.velocity = vel;
    }

    public enum Direction {
        // this representation scheme borrows from the "snake game" sample project
        NORTH(0, -1),
        EAST(1, 0),
        SOUTH(0, 1),
        WEST(-1, 0),
        NONE(0, 0);

        private final int dx;
        private final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        public int getDx() {
            return dx;
        }

        public int getDy() {
            return dy;
        }
    }
}
