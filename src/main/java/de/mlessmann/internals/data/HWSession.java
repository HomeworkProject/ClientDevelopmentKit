package de.mlessmann.internals.data;

import de.mlessmann.api.data.IHWSession;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Life4YourGames on 01.09.16.
 */
public class HWSession implements IHWSession {

    private JSONObject json;

    public HWSession(JSONObject json) {
        this.json = json;
    }

    @Override
    public JSONObject getJSON() {
        return json;
    }

    public JSONArray getValidUntil() {
        JSONArray a = json.optJSONArray("expires");
        if (a == null)
            a = new JSONArray();
        return a;
    }

    @Override
    public int[] expires() {

        JSONArray a = getValidUntil();

        return new int[]{
            a.optInt(0, 1),
                a.optInt(1, 1),
                a.optInt(2, 1),
                a.optInt(3, 0),
                a.optInt(4, 0)
        };

    }

    @Override
    public String group() {
        return json.optString("group", "NULL");
    }

    @Override
    public String user() {
        return json.optString("user", "NULL");
    }

    @Override
    public String getToken() {
        return json.optString("token", "NULL");
    }
}

