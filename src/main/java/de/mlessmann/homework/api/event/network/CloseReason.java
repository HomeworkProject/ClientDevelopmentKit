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
     * Failed to connect:
     * SLL failed and connection attempt got cancelled or plaintext failed
     * If cause is an exception {@link de.mlessmann.homework.api.event.ICDKExceptionEvent} is also implemented
     */
    CONNECT_FAILED,

    /**
     * Certificate related -> Cert wasn't accepted and plaintext got cancelled
     * equals DISCONNECTED
     */
    REJECTED_X509,

    /**
     * Connection has been softly disconnected
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