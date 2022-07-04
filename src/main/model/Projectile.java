package model;

import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

// Represents projectile in tank game
public class Projectile extends Vehicle implements Writable {
    public static final int MAX_DISTANCE = 36;
    public static final int BULLET_SPEED = 1;
    public static final int SEED_SPEED = 1;
    public static final int RAY_SPEED = 1;

    private int distance = 0;
    private final Type typ;

    // Constructs projectile of given type, at given position, with given direction
    public Projectile(Position pos, Direction dir, Type t) {
        super(pos);
        List<Position> body = new ArrayList<>();
        body.add(pos);
        this.setLocation(body);
        this.setDirection(dir);
        this.typ = t;
        switch (typ) {
            case BULLET:
                this.setVelocity(BULLET_SPEED);
                break;
            case SEED:
                this.setVelocity(SEED_SPEED);
                break;
            default:
                this.setVelocity(RAY_SPEED);
                break;
        }
    }

    // Getters:
    public int getDistance() {
        return this.distance;
    }

    public Type getType() {
        return this.typ;
    }

    // Enum of projectile types
    public enum Type {
        BULLET,
        SEED,
        DEATH_RAY,
        LIFE_RAY;

        // Outputs string representation of projectile type
        public String toString() {
            switch (this) {
                case SEED:
                    return "seed";
                case DEATH_RAY:
                    return "deathray";
                case LIFE_RAY:
                    return "liferay";
                default:
                    return "bullet";
            }
        }
    }

    // Modifies this
    // Moves and increments distance travelled
    @Override
    public void move() {
        super.move();
        switch (typ) {
            case BULLET:
                distance = this.distance + BULLET_SPEED;
                break;
            case SEED:
                distance = this.distance + SEED_SPEED;
                break;
            case DEATH_RAY:
            case LIFE_RAY:
                distance = this.distance + RAY_SPEED;
                break;
        }
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonProjectile = new JSONObject();
        jsonProjectile.put("x", this.getPosition().getX());
        jsonProjectile.put("y", this.getPosition().getY());
        jsonProjectile.put("type", this.getType().toString());
        jsonProjectile.put("dx", this.getDirection().getDx());
        jsonProjectile.put("dy", this.getDirection().getDy());
        jsonProjectile.put("dist", this.distance);
        return jsonProjectile;
    }

}