package de.mlessmann.homework.internal.event;

import de.mlessmann.homework.api.event.ICDKExceptionEvent;
import de.mlessmann.homework.api.event.network.CloseReason;

/**
 * Created by Life4YourGames on 02.01.17.
 */
public class CDKConnCloseExcEvent extends CDKConnCloseEvent implements ICDKExceptionEvent {

    private Exception exception;

    public CDKConnCloseExcEvent(Object sender, CloseReason closeReason, Exception exception) {
        super(sender, closeReason);
    }

    @Override
    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
