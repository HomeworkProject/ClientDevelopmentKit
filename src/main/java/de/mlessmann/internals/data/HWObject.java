package de.mlessmann.internals.data;

import de.mlessmann.api.data.IHWAttachment;
import de.mlessmann.api.data.IHWObj;
import de.mlessmann.common.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Life4YourGames on 10.08.16.
 */
public class HWObject implements IHWObj {

    public static HWObject dummy() {

        JSONObject j = new JSONObject();

        j.put("id", "null");
        j.put("subject", "null");
        j.put("date", new JSONArray());
        j.put("title", "null");
        j.put("desc", "null");
        j.put("dummy", true);

        return new HWObject(j);

    }

    private JSONObject src;

    public HWObject(JSONObject source) {
        src = source;
    }

    @Override
    public boolean isDummy() {
        return src.optBoolean("dummy", false);
    }

    @Override
    public String getId() {
        return src.getString("id");
    }

    @Override
    public int[] getDate() {
        JSONArray a = src.getJSONArray("date");
        return new int[]{a.getInt(0), a.getInt(1), a.getInt(2)};
    }

    @Override
    @Nullable
    public String getTitle() {
        return src.optString("title", null);
    }

    @Override
    public String optTitle(String def) {
        return src.optString("title", def);
    }

    @Override
    @Nullable
    public String getDescription() {
        return src.optString("desc", null);
    }

    @Override
    public String optDescription(String def) {
        return src.optString("desc", def);
    }

    @Override
    public String getSubject() {
        return src.getString("subject");
    }

    @Override
    public JSONObject getJSON() {
        return src;
    }

    @Override
    public int getAttachmentCount() {
        return getAttachments().size();
    }

    @Override
    public List<IHWAttachment> getAttachments() {
        List<IHWAttachment> l = new ArrayList<IHWAttachment>();

        JSONObject attachJSON = src.optJSONObject("attachments");
        if (attachJSON!=null) {
            //ForEach not possible due to the org.json lib in android
            Iterator<String> i = attachJSON.keys();
            while (i.hasNext()) {
                String k = i.next();
                JSONObject o = attachJSON.optJSONObject(k);
                if (o!=null && k.startsWith("position-")) {
                    String pos = k.substring("position-".length());
                    try {
                        int iPos = Integer.parseInt(pos);
                        HWAttachment a = new HWAttachment(o, iPos);
                        if (a.isValid()) l.add(a);
                    } catch (NumberFormatException e) {
                        //Invalid attachment, just skip
                    }
                }
            }
        }
        return l;
    }
}

