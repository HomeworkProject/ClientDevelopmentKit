package de.mlessmann.api.data;

import de.mlessmann.common.annotations.Nullable;

/**
 * Created by Life4YourGames on 16.11.16.
 */
public interface IFTToken {

    public enum Direction {
        POST,
        GET,
        UNKNOWN
    }

    @Nullable
    String getToken();

    IFTToken.Direction getDirection();
}
