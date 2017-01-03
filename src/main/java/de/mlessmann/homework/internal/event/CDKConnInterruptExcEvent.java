package de.mlessmann.homework.internal.event;

import de.mlessmann.homework.api.event.ICDKExceptionEvent;
import de.mlessmann.homework.api.event.network.InterruptReason;

/**
 * Created by Life4YourGames on 02.01.17.
 */
public class CDKConnInterruptExcEvent extends CDKConnInterruptEvent implements ICDKExceptionEvent {

    private Exception exception;

    public CDKConnInterruptExcEvent(Object sender) {
        super(sender, InterruptReason.SSL_UNAVAILABLE);
    }

    public CDKConnInterruptExcEvent(Object sender, InterruptReason reason, Exception exception) {
        super(sender, reason);
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
