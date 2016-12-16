package de.mlessmann.homework.internal.event;

import de.mlessmann.homework.api.ICDKConnection;
import de.mlessmann.homework.api.event.ICDKConnectionEvent;
import de.mlessmann.homework.api.event.network.ConnectionStatus;
import de.mlessmann.homework.api.event.network.InterruptReason;

/**
 * Created by magnus.lessmann on 16.12.2016.
 */
public class CDKConnInterruptEvent extends CDKConnEvent implements ICDKConnectionEvent.Interrupted {

    private InterruptReason reason;
    private boolean cancelled;

    public CDKConnInterruptEvent(Object sender, ICDKConnection connection, InterruptReason reason) {
        super(sender, connection, ConnectionStatus.CONNECTING_INTERRUPTED);
        this.reason = reason;
        this.cancelled = false;
    }

    @Override
    public InterruptReason getInterruptReason() {
        return reason;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean getCancelled() {
        return cancelled;
    }
}
