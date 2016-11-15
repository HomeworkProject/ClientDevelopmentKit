package de.mlessmann.internals.data;

import de.mlessmann.api.data.IHWAttachmentLocation;
import de.mlessmann.common.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Life4YourGames on 15.11.16.
 */
public class HWAttachmentLocation implements IHWAttachmentLocation {

    private int[] date;
    private String hwID;
    private String assetID;
    private String remoteURL;
    private String name = null;
    private LocationType type = LocationType.INVALID;

    public HWAttachmentLocation(JSONObject location) {
        if (location != null && (location.optString("name"))!=null) {
            String webLocation = location.optString("url");
            if (webLocation != null) {
                remoteURL = webLocation;
                type = LocationType.WEB;
            } else {
                String hwID = location.optString("ownerhw");
                JSONArray hwDate = location.optJSONArray("date");
                String id = location.optString("id");
                if (hwID != null && id != null && hwDate != null && hwDate.length() >= 3) {
                    try {
                        date = new int[]{hwDate.getInt(0), hwDate.getInt(1), hwDate.getInt(2)};
                        this.hwID = hwID;
                        this.assetID = id;
                        this.type = LocationType.SERVER;
                    } catch (Exception e) {
                        //Simple: still invalid
                    }
                }
            }
        }
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

    @Nullable
    public String getID() {
        return assetID;
    }

    @Override
    public IHWAttachmentLocation.LocationType getLocType() {
        return type;
    }


}
