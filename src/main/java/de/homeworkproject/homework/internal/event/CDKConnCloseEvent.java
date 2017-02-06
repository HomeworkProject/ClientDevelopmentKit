package de.homeworkproject.homework.internal.event;

import de.homeworkproject.homework.api.event.ICDKConnectionEvent;
import de.homeworkproject.homework.api.event.network.CloseReason;
import de.homeworkproject.homework.api.event.network.ConnectionStatus;

/**
 * Created by Life4YourGames on 02.01.17.
 */
public class CDKConnCloseEvent extends CDKConnEvent implements ICDKConnectionEvent.Closed {

    private CloseReason reason;

    public CDKConnCloseEvent(Object sender, CloseReason reason) {
        super(sender, ConnectionStatus.DISCONNECTED);
        setReason(reason);
    }

    @Override
    public CloseReason getCloseReason() {
        return reason;
    }

    public void setReason(CloseReason reason) {
        this.reason = reason;
    }
}
