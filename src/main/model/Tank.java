package model;

import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Represents tank in tank game
public class Tank extends Vehicle implements Writable {

    private static final int TANK_SPEED = 1;
    public static final Direction INIT_TANK_DIRECTION = Direction.NORTH;

    // Constructs tank at given position, with default direction and speed
    public Tank(Position pos) {
        super(pos);
        super.setVelocity(TANK_SPEED);
        super.setDirection(INIT_TANK_DIRECTION);
        List<Position> body = handleBody(pos, this.getDirection());
        super.setLocation(body);
    }

    // Modifies this
    // Changes direction to that given, and updates embodiment
    public void turn(Direction dir) {
        this.setDirection(dir);
        this.setLocation(this.handleBody(this.getPosition(), dir));
    }

    // Requires that handleBody() put cannon position at index = 1
    // Returns the position of the tank's cannon
    public Position getCanonPos() {
        return this.getLocation().get(1);
    }

    // Returns list of positions representing parts of the tank body
    // with tank's core at index = 0, cannon at index = 1
    private List<Position> handleBody(Position pos, Direction dir) {
        Position top = new Position(pos.getX(), pos.getY() - 1);
        Position right = new Position(pos.getX() + 1, pos.getY());
        Position bottom = new Position(pos.getX(), pos.getY() + 1);
        Position left = new Position(pos.getX() - 1, pos.getY());
        List<Position> body = new ArrayList<>();

        switch (dir) {
            case EAST:
                body.addAll(Arrays.asList(pos, right, top, bottom));
                break;
            case SOUTH:
                body.addAll(Arrays.asList(pos, bottom, right, left));
                break;
            case WEST:
                body.addAll(Arrays.asList(pos, left, bottom, top));
                break;
            default: // NORTH
                body.addAll(Arrays.asList(pos, top, left, right));
                break;
        }
        return body;
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonTank = new JSONObject();
        JSONObject jsonPos = new JSONObject();
        jsonPos.put("x", this.getPosition().getX());
        jsonPos.put("y", this.getPosition().getY());
        jsonTank.put("position", jsonPos);
        jsonTank.put("dx", this.getDirection().getDx());
        jsonTank.put("dy", this.getDirection().getDy());
        return jsonTank;
    }
}
