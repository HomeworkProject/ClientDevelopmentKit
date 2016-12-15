package de.mlessmann.homework.api.session;

import de.mlessmann.common.annotations.Nullable;

/**
 * Created by Life4YourGames on 09.08.16.
 */
public interface IHWUser {

    String group();

    String name();

    /**
     * Session information for "quick" login
     * Only available if server provided it.
     */
    @Nullable
    IHWSession session();
}