package de.mlessmann.internals.data;

import com.sun.istack.internal.Nullable;
import de.mlessmann.api.data.IHWObj;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Life4YourGames on 10.08.16.
 */
public class HWObject implements IHWObj {

    public static HWObject fromErrorCode(int e) {

        JSONObject j = new JSONObject();

        j.put("id", "null");
        j.put("subject", "null");
        j.put("date", new int[]{0,0,0});
        j.put("long", new JSONObject());
        j.put("short", new JSONObject());
        j.put("errorCode", e);

        return new HWObject(j);

    }

    private JSONObject src;

    public HWObject(JSONObject source) {
        src = source;
    }

    @Override
    public int errorCode() {
        return src.optInt("errorCode", -1);
    }

    @Override
    public String id() {
        return src.getString("id");
    }

    @Override
    public int[] date() {
        JSONArray a = src.getJSONArray("date");
        return new int[]{a.getInt(0), a.getInt(1), a.getInt(2)};
    }

    @Override
    @Nullable
    public String getDescription(boolean fromLongSrc) {
        return src.getJSONObject(fromLongSrc ? "long" : "short").optString("desc", null);
    }

    @Override
    public String optDescription(boolean fromLongSrc, String d) {
        return src.getJSONObject(fromLongSrc ? "long" : "short").optString("desc", d);
    }

    @Override
    public String subject() {
        return src.getString("subject");
    }

    @Override
    public JSONObject getJSON() {
        return src;
    }

}

