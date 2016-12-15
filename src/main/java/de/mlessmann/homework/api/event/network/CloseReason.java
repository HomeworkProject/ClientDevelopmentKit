package de.mlessmann.homework.api.event.network;

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
     * Connection has been softly disconnected
     * e.g. when an connection attempt has been canceled
     */
    DISCONNECTED,

    /**
     * Connection has been killed via #kill()
     */
    KILLED,

    /**
     * Connection has been closed by the remote host
     */
    LOST,

    /**
     * Timeout is only used by the #connect future
     */
    TIMEOUT
}