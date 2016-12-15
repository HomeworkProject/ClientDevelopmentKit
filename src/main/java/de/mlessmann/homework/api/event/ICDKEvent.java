package de.mlessmann.homework.api.event;

import de.mlessmann.common.annotations.Nullable;
import de.mlessmann.homework.api.ICDKConnection;

/**
 * Created by Life4YourGames on 15.12.16.
 */
public interface ICDKEvent {

    /**
     * If the event passed through a CDKConnection this returns the connection
     */
    @Nullable
    ICDKConnection getConnection();

    /**
     * The sender of the Event
     */
    Object getSender();
}
