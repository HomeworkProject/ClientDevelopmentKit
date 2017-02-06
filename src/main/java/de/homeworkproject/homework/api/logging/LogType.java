package de.homeworkproject.homework.api.logging;

/**
 * Created by Life4YourGames on 30.08.16.
 * Class which purpose is listing all available log-event types.
 */
public interface LogType {
    /**
     * Payload will be the message as String
     */
    public static String LOGMESSAGE = "logmsg";

    /**
     * Payload will be an exception
     */
    public static String EXC = "except";

    /**
     * Internal cdk errors such as caught exceptions
     * Payload will be the message as String
     */
    public static String CDKMSG = "cdkmsg";

    /**
     * Internal cdk errors such as caught exceptions
     * Payload will be the Exception
     * @see Exception
     */
    public static String CDKEXC = "cdkexcept";
}