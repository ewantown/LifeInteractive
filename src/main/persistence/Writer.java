package persistence;

import org.json.JSONObject;
import ui.Game;
import ui.Surface;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

// Representation of a Json file writer
// This class borrows heavily from the provided Serialization Demo
public class Writer {

    private PrintWriter writer;
    private final String target;

    // Constructs JSON writer
    public Writer(String target) {
        this.target = jsonPath(target);
    }

    // Opens target file, throws exception if path to target not found
    public void open() throws FileNotFoundException {
        writer = new PrintWriter(target);
    }

    // Modifies target
    // Writes representation of game state to file as string
    public void writeGameState(Surface surface, Game game) {
        JSONObject state = new JSONObject();
        state.put("surface", surface.toJson());
        state.put("game", game.toJson());
        state.put("score", game.getScore());
        writer.print(state);
    }

    // Closes writer
    public void close() {
        writer.close();
    }

    // Returns result of appending ".json" to end of file's path string if not already present
    private String jsonPath(String path) {
        if (path.endsWith(".json")) {
            return path;
        } else {
            return path + ".json";
        }
    }

}
