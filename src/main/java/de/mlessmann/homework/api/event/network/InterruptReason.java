package de.mlessmann.homework.api.event.network;

/**
 * Created by Life4YourGames on 15.12.16.
 * Connection attempt has been interrupted
 */
public enum InterruptReason {

    /**
     * Unknown reason
     */
    UNKNOWN,

    /**
     * SSL connection failed -> if not cancelled the connection will be tried via. a plaintext connection
     *
     * Note that this is about the socket failing to connect, neither certificates nor exceptions
     * But it may indicate an exception during SSLContext-Setup
     * * In this case an {@link de.mlessmann.homework.api.event.ICDKExceptionEvent} will follow
     */
    SSL_UNAVAILABLE,

    /**
     * Certificate related -> Will allow trusting the certificate to connect anyways
     * -> Will recheck the certificate if not cancelled
     */
    REJECTING_X509,

    /**
     * Certificate related -> if not cancelled the connection will be tried via. a plaintext connection
     */
    REJECTED_X509
}
