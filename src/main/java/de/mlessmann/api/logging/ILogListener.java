package de.mlessmann.api.logging;

/**
 * Created by Life4YourGames on 29.08.16.
 */
public interface ILogListener {

    /**
     * Called by LogMgr
     * @see IHWLogContext
     */
    void onMessage(IHWLogContext context);

}
