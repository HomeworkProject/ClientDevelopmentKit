package de.mlessmann.homework.api.event;

import de.mlessmann.homework.api.event.network.CloseReason;
import de.mlessmann.homework.api.event.network.ConnectionStatus;
import de.mlessmann.homework.api.event.network.InterruptReason;

import java.security.cert.X509Certificate;

/**
 * Created by Life4YourGames on 15.12.16.
 */
public interface ICDKConnectionEvent extends ICDKEvent {

    ConnectionStatus getStatus();

    public interface Closed extends ICDKConnectionEvent {

        CloseReason getCloseReason();
    }

    public interface Interrupted extends ICDKConnectionEvent {

        InterruptReason getInterruptReason();

        /**
         * Cancel connection:
         * e.g. when REJECTED_X509 was received and a plaintext connection should not be established
         * @param cancelled Whether or not to cancel the connection attempt
         */
        void setCancelled(boolean cancelled);

        boolean isCancelled();

        public interface X509RejectInterrupt extends ICDKConnectionEvent.Interrupted {

            /**
             * Return the certificate chain
             */
            X509Certificate[] getChain();

            /**
             * Allows to exempt the certificate once
             * without having to trust it
             */
            void exemptOnce(boolean exemptOnce);
        }
    }
}
