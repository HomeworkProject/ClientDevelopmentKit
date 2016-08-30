package de.mlessmann.internals.logging;

import de.mlessmann.api.logging.IHWLogContext;

/**
 * Created by Life4YourGames on 30.08.16.
 */
public class LogContext implements IHWLogContext {

    private int level;
    private String type;
    private Object payload;

    public LogContext(int level, String type, Object payload) {
        this.level = level;
        this.type = type;
        this.payload = payload;
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
