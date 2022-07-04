package model;

import org.junit.jupiter.api.Assertions;

import java.util.List;

// Unit tests for the vehicle class
public abstract class VehicleTest {

    Vehicle vehicle;

    void nextLocationTest() {
        List<Position> curLoc = vehicle.getLocation();
        List<Position> nextLoc = vehicle.nextLocation();
        int dx = vehicle.getDirection().getDx();
        int dy = vehicle.getDirection().getDy();
        Position bodyPart;
        Position nextPart;
        for (int i = 0; i < curLoc.size(); i++) {
            bodyPart = curLoc.get(i);
            nextPart = nextLoc.get(i);
            Assertions.assertEquals(bodyPart.getX() + (vehicle.getVelocity() * dx), nextPart.getX());
            Assertions.assertEquals(bodyPart.getY() + (vehicle.getVelocity() * dy), nextPart.getY());
        }
    }

    void moveTest() {
        List<Position> nextLoc = vehicle.nextLocation();
        vehicle.move();
        Assertions.assertEquals(nextLoc, vehicle.getLocation());
    }

    void setPositionTest() {
        Position p0 = new Position(0, 0);
        Position p1 = new Position(1, 1);
        vehicle.setPosition(p0);
        Assertions.assertEquals(p0, vehicle.getPosition());
        vehicle.setPosition(p1);
        Assertions.assertEquals(p1, vehicle.getPosition());
    }
}
