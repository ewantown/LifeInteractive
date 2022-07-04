package persistence;

import org.json.JSONObject;

public interface Writable {
    // Returns this as JSON object
    JSONObject toJson();
}
