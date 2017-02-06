package de.homeworkproject.homework.internal.logging;

import de.homeworkproject.homework.api.CDK;
import de.homeworkproject.homework.api.ICDKConnection;
import de.homeworkproject.homework.api.event.ICDKLogEvent;
import de.homeworkproject.homework.api.logging.IHWLogContext;
import de.homeworkproject.homework.api.logging.LogType;

/**
 * Created by Life4YourGames on 29.08.16.
 */
public class LogManager {

    private CDK cdk;

    public LogManager(CDK cdk) {
        this.cdk = cdk;
    }

    //----------------------------------- Logging ---------------------------------------------

    public synchronized void sendLog(final IHWLogContext context) {
        cdk.fireEvent(
                new ICDKLogEvent() {
                    @Override
                    public IHWLogContext getContext() {
                        return context;
                    }

                    @Override
                    public ICDKConnection getConnection() {
                        return null;
                    }

                    @Override
                    public Object getSender() {
                        return this;
                    }
                }
        );
    }

    public synchronized void sendLog(Object sender, int level, String msg) {
        LogContext c = new LogContext(sender, level, LogType.LOGMESSAGE, msg);
        sendLog(c);
    }

    public synchronized void cdk_sendLog(Object sender, int level, String msg) {
        LogContext c = new LogContext(sender, level, LogType.CDKMSG, msg);
        sendLog(c);
    }

    public synchronized void sendLog(Object sender, int level, Exception e) {
        LogContext c = new LogContext(sender, level, LogType.EXC, e);
        sendLog(c);
    }

    public synchronized void cdk_sendLog(Object sender, int level, Exception e) {
        LogContext c = new LogContext(sender, level, LogType.CDKEXC, e);
        sendLog(c);
    }
}