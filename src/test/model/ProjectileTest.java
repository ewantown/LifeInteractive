package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Unit tests for the Projectile class
public class ProjectileTest extends VehicleTest {
    Projectile bullet;
    Projectile seed;
    Projectile deathray;
    Projectile liferay;
    Set<Projectile> set;


    @BeforeEach
    void setup() {
        bullet = new Projectile(new Position(10, 10), Vehicle.Direction.NORTH, Projectile.Type.BULLET);
        vehicle = bullet;
        seed = new Projectile(new Position(11, 11), Vehicle.Direction.EAST, Projectile.Type.SEED);
        deathray = new Projectile(new Position(12, 12), Vehicle.Direction.SOUTH, Projectile.Type.DEATH_RAY);
        liferay = new Projectile(new Position(13, 13), Vehicle.Direction.WEST, Projectile.Type.LIFE_RAY);
        set = Stream.of(bullet, seed, deathray, liferay).collect(Collectors.toSet());
    }

    @Test
    void testSetup () {
        Assertions.assertEquals(Projectile.Type.BULLET, bullet.getType());
        Assertions.assertEquals(Projectile.Type.SEED, seed.getType());
        Assertions.assertEquals(Projectile.Type.LIFE_RAY, liferay.getType());
        Assertions.assertEquals(Projectile.Type.DEATH_RAY, deathray.getType());
    }

    @Test
    void moveTest() {
        for (Projectile p : set) {
            List<Position> nextLoc = p.nextLocation();
            Assertions.assertEquals(0, p.getDistance());
            p.move();
            Assertions.assertEquals(nextLoc, p.getLocation());
            Assertions.assertEquals(p.getVelocity(), p.getDistance());
        }
    }

    @Test
    void typeToStringTest() {
        Assertions.assertEquals("liferay", liferay.getType().toString());
        Assertions.assertEquals("deathray", deathray.getType().toString());
        Assertions.assertEquals("bullet", bullet.getType().toString());
        Assertions.assertEquals("seed", seed.getType().toString());
    }

    @Test
    void nextLocationAbstractTest() {
        nextLocationTest();
    }

    @Test
    void setPositionAbstractTest() {
        setPositionTest();
    }

}
