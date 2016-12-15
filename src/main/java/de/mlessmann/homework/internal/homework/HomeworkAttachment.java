package de.mlessmann.homework.internal.homework;

import de.mlessmann.common.annotations.Nullable;
import de.mlessmann.homework.api.homework.IHomeworkAttachment;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Life4YourGames on 03.11.16.
 */
public class HomeworkAttachment implements IHomeworkAttachment {

    private int position;
    private int[] date;
    private String hwID;
    private String assetID;
    private String remoteURL;
    private String name = null;
    private LocationType type = LocationType.INVALID;
    private JSONObject json;

    public HomeworkAttachment(JSONObject attachmentJSON) {
        json = attachmentJSON;

        JSONArray hwDate = json.optJSONArray("date");
        if (hwDate != null && hwDate.length() >= 3) {
            try {
                date = new int[]{hwDate.getInt(0), hwDate.getInt(1), hwDate.getInt(2)};
            } catch (Exception e) {
                //Simple: still invalid
                return;
            }
        }
        hwID = json.optString("ownerhw", null);
        if (hwID==null) return;

        if (json != null && (json.optString("name", null))!=null) {
            String webLocation = json.optString("url");
            if (webLocation != null) {
                remoteURL = webLocation;
                type = LocationType.WEB;
            } else {
                assetID = json.optString("id", null);
                if (assetID!=null || isVirtual()) {
                    this.type = LocationType.SERVER;
                }
            }
        }
    }

    public boolean isValid() {
        return type != LocationType.INVALID;
    }

    @Override
    public String getTitle() {
        return json.optString("title", null);
    }

    @Override
    public String getDescription() {
        return json.optString("desc", null);
    }

    @Override
    public String getID() {
        return assetID;
    }

    public boolean isVirtual() {
        return json.has("virtual");
    }

    public String getName() { return name; }

    @Nullable
    @Override
    public String getURL() {
        return remoteURL;
    }

    @Nullable
    @Override
    public int[] getDate() {
        return date;
    }

    @Nullable
    @Override
    public String getHWID() {
        return hwID;
    }

    @Override
    public IHomeworkAttachment.LocationType getLocType() {
        return type;
    }

    @Override
    public JSONObject getJSON() {
        return json;
    }
}