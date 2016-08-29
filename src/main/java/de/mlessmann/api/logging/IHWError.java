package de.mlessmann.api.logging;

/**
 * Created by Life4YourGames on 29.08.16.
 */
public interface IHWError {

    String getMessage();

    String getType();

    /**
     * @see LogLevel
     */
    int getLevel();

    public class Types {


        public static String LOGMESSAGE = "logmsg";

        /**
         * Internal cdk errors such as caught exceptions
         */
        public static String CDKERROR = "cdkerror";

    }

}
