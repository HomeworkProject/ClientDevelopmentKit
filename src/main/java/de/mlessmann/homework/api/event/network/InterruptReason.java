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
     * Note that this is about the socket failing to connect and *WILL NOT* have anything to do with the certificate
     * But it may indicate an exception during SSLContext-Setup
     * * In this case an {@link de.mlessmann.homework.api.event.ICDKLogEvent} carrying an exception will follow
     */
    SSL_UNAVAILABLE,

    /**
     * Certificate related -> Will allow trusting the certificate to connect anyways
     * -> Will recheck the certificate if not cancelled

     * Will be an instance of {@link de.mlessmann.homework.api.event.ICDKConnectionEvent.Interrupted.X509RejectInterrupt}
     */
    REJECTING_X509,

    /**
     * Certificate related -> if not cancelled the connection will be tried via. a plaintext connection

     * Will be an instance of {@link de.mlessmann.homework.api.event.ICDKConnectionEvent.Interrupted.X509RejectInterrupt}
     */
    REJECTED_X509
}
