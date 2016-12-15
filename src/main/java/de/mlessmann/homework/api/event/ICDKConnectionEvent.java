package de.mlessmann.homework.api.event;

import de.mlessmann.homework.api.event.network.CloseReason;
import de.mlessmann.homework.api.event.network.InterruptReason;

/**
 * Created by Life4YourGames on 15.12.16.
 */
public interface ICDKConnectionEvent extends ICDKEvent {

    ICDKConnectionEvent.Status getStatus();

    public enum Status {
        UNKNOWN,
        CONNECTING,
        CONNECTING_INTERRUPTED,
        CONNECTED,
        DISCONNECTED
    }

    public interface Closed {

        CloseReason getCloseReason();
    }

    public interface Interrupted {

        InterruptReason getInterruptReason();

        /**
         * Cancel connection:
         * e.g. when REJECTED_X509 was received and a plaintext connection should not be established
         * @param cancelled Whether or not to cancel the connection attempt
         */
        void setCancelled(boolean cancelled);
    }
}
