package de.mlessmann.api.networking;

import de.mlessmann.internals.networking.requests.RequestMgr;

/**
 * Created by Life4YourGames on 25.09.16.
 */
public enum CloseReason {

    /**
     * Unknown reason
     */
    UNKNOWN,

    /**
     * An exception occurred during I/O
     */
    EXCEPTION,

    /**
     * ReqMgr has been killed via #kill()
     * @see RequestMgr#kill()
     */
    KILL,

    /**
     * Connection has been closed by the remote host
     */
    LOST,

    /**
     * Timeout is only used by the #connect future
     */
    TIMEOUT
}
