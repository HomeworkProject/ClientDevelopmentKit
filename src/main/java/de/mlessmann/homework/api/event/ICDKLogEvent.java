package de.mlessmann.homework.api.event;

import de.mlessmann.homework.api.logging.IHWLogContext;

/**
 * Created by Life4YourGames on 15.12.16.
 */
public interface ICDKLogEvent extends ICDKEvent {

    IHWLogContext getContext();
}
