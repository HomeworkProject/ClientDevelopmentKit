package de.homeworkproject.homework.internal.event;

import de.homeworkproject.homework.api.ICDKConnection;
import de.homeworkproject.homework.api.event.ICDKLogEvent;
import de.homeworkproject.homework.api.logging.IHWLogContext;

/**
 * Created by magnus.lessmann on 16.12.2016.
 */
public class CDKLogEvent extends CDKEvent implements ICDKLogEvent {

    private IHWLogContext ctx;

    public CDKLogEvent(Object sender, ICDKConnection connection, IHWLogContext ctx) {
        super(sender, connection);
        this.ctx = ctx;
    }

    @Override
    public IHWLogContext getContext() {
        return ctx;
    }
}