package de.mlessmann.api.data;

import de.mlessmann.common.annotations.Nullable;
import org.json.JSONObject;

/**
 * Created by Life4YourGames on 15.11.16.
 */
public interface IHWAttachmentLocation {

    LocationType getLocType();

    String getName();

    @Nullable
    String getHWID();

    @Nullable
    String getID();

    @Nullable
    int[] getDate();

    @Nullable
    String getURL();

    JSONObject getJSON();

    public enum LocationType {
        SERVER,
        WEB,
        INVALID
    }
}
