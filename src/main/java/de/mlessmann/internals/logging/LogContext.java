package de.mlessmann.internals.logging;

import de.mlessmann.api.logging.IHWLogContext;

/**
 * Created by Life4YourGames on 30.08.16.
 */
public class LogContext implements IHWLogContext {

    private Object sender;
    private int level;
    private String type;
    private Object payload;

    public LogContext(Object sender, int level, String type, Object payload) {
        this.sender = sender;
        this.level = level;
        this.type = type;
        this.payload = payload;
    }

    @Override
    public Object getSender() {
        return sender;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Object getPayload() {
        return payload;
    }

}
