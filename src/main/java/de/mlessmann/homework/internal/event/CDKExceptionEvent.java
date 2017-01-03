package de.mlessmann.homework.internal.event;

import de.mlessmann.homework.api.event.ICDKExceptionEvent;

/**
 * Created by Life4YourGames on 02.01.17.
 */
public class CDKExceptionEvent extends CDKEvent implements ICDKExceptionEvent {

    private Exception exception;

    public CDKExceptionEvent(Object sender, Exception exception) {
        super(sender);
        setException(exception);
    }

    @Override
    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
