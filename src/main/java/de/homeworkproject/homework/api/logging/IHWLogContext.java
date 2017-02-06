package de.homeworkproject.homework.api.logging;

/**
 * Created by Life4YourGames on 29.08.16.
 *
 * Class used to store Log events fired by the CDK
 *
 * Note: One event may fire multiple times for different LogLevels.
 * E.g.:
 * "INFO(logmsg) - An error has occurred:"
 * followed by
 * "INFO(cdkerror) - Error while reading socket"
 * followed by
 * "ERROR(cdkexc) - IOException"
 *
 * Though this hasn't to be the case always, it's highly recommended not to ignore any
 * of the levels or types.
 * (Meaning that all events should somehow reach the logger,
 * even if that excludes the log level anyways/afterwards.)
 *
 */
public interface IHWLogContext {

    /**
     * Sender of the LogEntry
     */
    Object getSender();

    /**
     * Payload of the context can e.g. be a String, an Exception or sth. else
     * @see #getType() for further info
     */
    Object getPayload();

    /**
     * @see ILogLevel
     */
    int getLevel();


    /**
     * Defines type of payload
     * @see LogType
     */
    String getType();

}