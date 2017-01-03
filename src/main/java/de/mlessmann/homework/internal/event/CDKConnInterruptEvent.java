package de.mlessmann.homework.internal.event;

import de.mlessmann.homework.api.event.ICDKConnectionEvent;
import de.mlessmann.homework.api.event.network.ConnectionStatus;
import de.mlessmann.homework.api.event.network.InterruptReason;

/**
 * Created by magnus.lessmann on 16.12.2016.
 */
public class CDKConnInterruptEvent extends CDKConnEvent implements ICDKConnectionEvent.Interrupted {

    private InterruptReason reason;
    private boolean cancelled;

    public CDKConnInterruptEvent(Object sender, InterruptReason reason) {
        super(sender, ConnectionStatus.CONNECTING_INTERRUPTED);
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
