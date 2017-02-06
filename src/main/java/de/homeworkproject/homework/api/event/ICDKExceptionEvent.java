package de.homeworkproject.homework.api.event;

/**
 * Created by Life4YourGames on 15.12.16.
 */
public interface ICDKExceptionEvent extends ICDKEvent {

    Exception getException();
}
