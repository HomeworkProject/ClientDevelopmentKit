package de.mlessmann.api.data;

import de.mlessmann.common.annotations.Nullable;

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

    public enum LocationType {
        SERVER,
        WEB,
        INVALID
    }
}
