package de.mlessmann.internals.logging;

import de.mlessmann.api.logging.IHWLogContext;
import de.mlessmann.api.logging.ILogListener;

import java.util.ArrayList;

import static de.mlessmann.api.logging.Types.*;

/**
 * Created by Life4YourGames on 29.08.16.
 */
public class LMgr {

    private ArrayList<ILogListener> listeners;

    public LMgr() {

        listeners = new ArrayList<ILogListener>();

    }

    //----------------------------------- Logging ---------------------------------------------

    public synchronized void sendLog(IHWLogContext context) {

        for (int i = listeners.size() - 1; i >= 0 ; i--) {
            ILogListener l = listeners.get(i);
            l.onMessage(context);
        }

    }

    public synchronized void sendLog(Object sender, int level, String msg) {
        LogContext c = new LogContext(sender, level, LOGMESSAGE, msg);
        sendLog(c);
    }

    public synchronized void cdk_sendLog(Object sender, int level, String msg) {
        LogContext c = new LogContext(sender, level, CDKMSG, msg);
        sendLog(c);
    }

    public synchronized void sendLog(Object sender, int level, Exception e) {
        LogContext c = new LogContext(sender, level, EXC, e);
        sendLog(c);
    }

    public synchronized void cdk_sendLog(Object sender, int level, Exception e) {
        LogContext c = new LogContext(sender, level, CDKEXC, e);
        sendLog(c);
    }

    public synchronized void reportClosed(boolean byException) {
        for (int i = listeners.size() - 1; i >= 0 ; i--) {
            ILogListener l = listeners.get(i);
            l.onConnectionLost(byException);
        }
    }

    //----------------------------------- Register --------------------------------------------

    /**
     * Register a new listener
     * The listener will be notified on all future events
     * @param l The listener to register
     */
    public void registerListener(ILogListener l) {
        if (!listeners.contains(l))
            listeners.add(l);
    }

    /**
     * Unregister a previously registered listener
     * The listener will stop receiving events
     * @param l The listener to unregister
     */
    public void unregisterListener(ILogListener l) {
        listeners.remove(l);
    }

}
