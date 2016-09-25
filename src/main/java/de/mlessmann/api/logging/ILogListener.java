package de.mlessmann.api.logging;

import de.mlessmann.api.networking.CloseReason;

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
