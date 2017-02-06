package de.homeworkproject.homework.internal.event;

import de.homeworkproject.homework.api.ICDKConnection;
import de.homeworkproject.homework.api.event.ICDKEvent;

/**
 * Created by magnus.lessmann on 16.12.2016.
 */
public class CDKEvent implements ICDKEvent {

    private ICDKConnection connection;
    private Object sender;

    public CDKEvent(Object sender) {
        this.sender = sender;
    }

    public CDKEvent(Object sender, ICDKConnection connection) {
        this(sender);
        this.connection = connection;
    }

    @Override
    public ICDKConnection getConnection() {
        return connection;
    }
    public void setConnection(ICDKConnection connection) {
        this.connection = connection;
    }

    @Override
    public Object getSender() {
        return sender;
    }
}
