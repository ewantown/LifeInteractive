package persistence;

import model.Position;
import model.Projectile;
import model.Vehicle;
import org.json.JSONArray;
import org.json.JSONObject;
import ui.Game;
import ui.Main;
import ui.Surface;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

// Represents a reader that reads from a JSON data file
// The design of this class borrows from the "serialization" demo provided
public class Reader {
    private final String source;

    // Constructs Json source reader
    public Reader(java.lang.String src) {
        source = src;
    }

    // Reads a game state from file
    // throws IOException if error reading files
    public Game read() throws IOException {
        String data = dataFromStore(source);
        JSONObject json = new JSONObject(data);
        if (!json.isNull("surface")) {
            return parser(json);
        } else {
            throw new IOException("Data is not game state");
        }
    }

    // Returns content of source file as string
    // Throws exception if no file at src, src is not JSON, or src is not app data
    private String dataFromStore(String src) throws IOException {
        StringBuilder data = new StringBuilder();
        try {
            Files.lines(Paths.get(java.lang.String.valueOf(src)), StandardCharsets.UTF_8)
                    .forEach(data::append);
        } catch (IOException e) {
            throw new IOException("Error fetching data from store");
        }
        return data.toString();
    }

    // Parses JSON object as game
    private Game parser(JSONObject json) {
        JSONObject jsonSurface = json.getJSONObject("surface");
        JSONObject jsonLife = json.getJSONObject("game").getJSONObject("life");
        JSONObject jsonTank = json.getJSONObject("game").getJSONObject("tank");
        JSONArray jsonProjectiles = json.getJSONObject("game").getJSONArray("projectiles");

        int shiftX = (Main.SURFACE.getWidth() - jsonSurface.getInt("width")) / 2;
        int shiftY = (Main.SURFACE.getHeight() - jsonSurface.getInt("height")) / 2;

        Function<Surface, Set<Position>> footprintFn = s -> {
            JSONArray jsonCells = jsonLife.getJSONArray("cells");
            Set<Position> out = new HashSet<>();
            for (int i = 0; i < jsonCells.length(); i++) {
                JSONObject o = jsonCells.getJSONObject(i);
                out.add(new Position(o.getInt("x") + shiftX, o.getInt("y") + shiftY));
            }
            return out;
        };

        Game game = new Game(Main.SURFACE, footprintFn);

        putTank(game, jsonTank, shiftX, shiftY);

        putProjectiles(game, jsonProjectiles, shiftX, shiftY);

        game.getLife().addGenerations(jsonLife.getInt("generations"));

        game.setScore(json.getInt("score"));

        return game;
    }

    // Parses JSON object as projectile and returns direction
    private Vehicle.Direction parseDirection(JSONObject o) {
        Vehicle.Direction direction;
        switch (o.getInt("dx")) {
            case -1:
                direction = Vehicle.Direction.WEST;
                break;
            case 1:
                direction = Vehicle.Direction.EAST;
                break;
            default:
                switch (o.getInt("dy")) {
                    case -1:
                        direction = Vehicle.Direction.NORTH;
                        break;
                    case 1:
                        direction = Vehicle.Direction.SOUTH;
                        break;
                    default:
                        direction = Vehicle.Direction.NONE;
                }
        }
        return direction;
    }

    // Parses JSON object as projectile and returns type
    private Projectile.Type parseProjectileType(JSONObject o) {
        Projectile.Type typ;
        switch (o.getString("type")) {
            case "seed":
                typ = Projectile.Type.SEED;
                break;
            case "deathray":
                typ = Projectile.Type.DEATH_RAY;
                break;
            case "liferay":
                typ = Projectile.Type.LIFE_RAY;
                break;
            default:
                typ = Projectile.Type.BULLET;
        }
        return typ;
    }

    // Modifies g
    // Sets tank position and location
    private void putTank(Game g, JSONObject jsonTank, int shiftX, int shiftY) {
        int tankX = jsonTank.getJSONObject("position").getInt("x") + shiftX;
        int tankY = jsonTank.getJSONObject("position").getInt("y") + shiftY;
        if (tankX > 0 & tankX < Main.SURFACE.getWidth() & tankY > 0 & tankY < Main.SURFACE.getHeight()) {
            g.getTank().setPosition(new Position(tankX, tankY));
            g.getTank().turn(parseDirection(jsonTank));
        }
    }

    // Modifies g
    // Adds projectiles to game
    private void putProjectiles(Game g, JSONArray jsonProjectiles, int shiftX, int shiftY) {
        for (int i = 0; i < jsonProjectiles.length(); i++) {
            JSONObject o = jsonProjectiles.getJSONObject(i);
            int x = o.getInt("x") - (o.getInt("dx") * o.getInt("dist"));
            int y = o.getInt("y") - (o.getInt("dy") * o.getInt("dist"));
            Position posMinus = new Position(x + shiftX, y + shiftY);
            Projectile p = new Projectile(posMinus, parseDirection(o), parseProjectileType(o));
            for (int k = 0; k <= o.getInt("dist"); k++) {
                p.move();
            }
            g.getProjectiles().add(p);
        }
    }
}