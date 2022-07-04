package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

// Unit tests for the Tank class
public class TankTest extends VehicleTest {

    int arbX = 100;
    int arbY = 100;

    int ctrX = arbX / 2;
    int ctrY = arbY / 2;

    Position pos = new Position(ctrX, ctrY);

    Tank tank;

    @BeforeEach
    public void setup() {
        tank = new Tank(pos);
        vehicle = new Tank(pos);
    }

    @Test
    public void setupTest() {
        Assertions.assertEquals(Tank.INIT_TANK_DIRECTION, tank.getDirection());
        Assertions.assertEquals(pos, tank.getPosition());
    }

    @Test
    public void turnCurrentDirectionTest() {
        setupTest();
        tank.turn(Tank.INIT_TANK_DIRECTION);
        Assertions.assertEquals(Tank.INIT_TANK_DIRECTION, tank.getDirection());
        int cannonX = tank.getPosition().getX() + (tank.getDirection().getDx() * tank.getVelocity());
        int cannonY = tank.getPosition().getX() + (tank.getDirection().getDy() * tank.getVelocity());
        Position cannon = new Position(cannonX, cannonY);
        Assertions.assertEquals(cannon, tank.getCanonPos());
    }

    @Test
    public void turnNewDirectionTest() {
        setupTest();

        tank.turn(Vehicle.Direction.EAST);
        Assertions.assertEquals(new Position(pos.getX() + 1, pos.getY()), tank.getCanonPos());
        Assertions.assertEquals(Vehicle.Direction.EAST, tank.getDirection());

        tank.turn(Vehicle.Direction.SOUTH);
        Assertions.assertEquals(new Position(pos.getX(), pos.getY() + 1), tank.getCanonPos());
        Assertions.assertEquals(Vehicle.Direction.SOUTH, tank.getDirection());

        tank.turn(Vehicle.Direction.WEST);
        Assertions.assertEquals(new Position(pos.getX() - 1, pos.getY()), tank.getCanonPos());
        Assertions.assertEquals(Vehicle.Direction.WEST, tank.getDirection());

        tank.turn(Vehicle.Direction.NORTH);
        Assertions.assertEquals(new Position(pos.getX(), pos.getY() - 1), tank.getCanonPos());
        Assertions.assertEquals(Vehicle.Direction.NORTH, tank.getDirection());
    }

    @Test
    void nextLocationAbstractTest() {
        setupTest();
        nextLocationTest();
    }

    @Test
    void moveAbstractTest() {
        setupTest();
        moveTest();
    }
}
