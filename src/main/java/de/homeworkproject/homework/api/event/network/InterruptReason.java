package de.homeworkproject.homework.api.event.network;

import de.homeworkproject.homework.api.ICDKConnection;
import de.homeworkproject.homework.api.event.ICDKConnectionEvent;
import de.homeworkproject.homework.api.event.ICDKLogEvent;

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
     * RequestVersion returned a negative value
     * {@link ICDKConnectionEvent.Interrupted#setCancelled(boolean)} determines whether to close or not
     * if cancelled, connection is terminated using {@link ICDKConnection#close()} thus, a close event follows.
     */
    POSSIBLY_INCOMPATIBLE,

    /**
     * SSL connection failed -> if not cancelled the connection will be tried via. a plaintext connection
     *
     * Note that this is about the socket failing to connect and *WILL NOT* have anything to do with the certificate
     * But it may indicate an exception during SSLContext-Setup
     * * In this case an {@link ICDKLogEvent} carrying an exception will follow
     */
    SSL_UNAVAILABLE,

    /**
     * Certificate related -> Will allow trusting the certificate to connect anyways
     * -> Will recheck the certificate if not cancelled

     * Will be an instance of {@link ICDKConnectionEvent.Interrupted.X509RejectInterrupt}
     */
    REJECTING_X509

    /**
     * Certificate related -> if not cancelled the connection will be tried via. a plaintext connection

     * Will be an instance of {@link ICDKConnectionEvent.Interrupted.X509RejectInterrupt}
     */
    //"Changed" TO CloseReason
    //REJECTED_X509
}
