package de.mlessmann.homework.api.logging;

import de.mlessmann.homework.api.network.CloseReason;

/**
 * Created by Life4YourGames on 29.08.16.
 */
public interface ILogListener {

    /**
     * Called by LogMgr
     * @see IHWLogContext
     */
    void onMessage(IHWLogContext context);

    /**
     * Called by reqMgr when connection is being detected as closed
     */
    void onConnectionLost(CloseReason rsn);
}