package de.mlessmann.homework.internal.event;

import de.mlessmann.homework.api.ICDKConnection;
import de.mlessmann.homework.api.event.ICDKConnectionEvent;
import de.mlessmann.homework.api.event.network.ConnectionStatus;
import de.mlessmann.homework.internal.CDKConnection;

/**
 * Created by magnus.lessmann on 16.12.2016.
 */
public class CDKConnEvent extends CDKEvent implements ICDKConnectionEvent {

    private ConnectionStatus status;

    public CDKConnEvent(Object sender, ICDKConnection connection, ConnectionStatus status) {
        super(sender, connection);
        this.status = status;
    }

    @Override
    public ConnectionStatus getStatus() {
        return status;
    }
}
