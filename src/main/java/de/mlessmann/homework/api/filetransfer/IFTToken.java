package de.mlessmann.homework.api.filetransfer;

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

    Direction getDirection();

    int getPort();
}
