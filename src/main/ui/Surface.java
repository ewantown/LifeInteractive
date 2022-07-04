package ui;

import org.json.JSONObject;
import persistence.Writable;

// Representation of a two-dimensional surface
public class Surface implements Writable {
    private final int width;
    private final int height;

    // Constructs surface of given width and height
    // to be passed by App to Game constructor
    public Surface(int x, int y) {
        width = x;
        height = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public JSONObject toJson() {
        JSONObject jsonSurface = new JSONObject();
        jsonSurface.put("width", this.width);
        jsonSurface.put("height", this.height);
        return jsonSurface;
    }
}



