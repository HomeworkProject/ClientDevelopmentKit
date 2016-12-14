package de.mlessmann.homework.api.logging;

/**
 * Created by Life4YourGames on 29.08.16.
 */
public interface ILogLevel {

    public static int UNKNOWN  = -1;

    public static int FINEST = 0;
    public static int FINER = 1;
    public static int FINE = 2;
    public static int INFO = 3;
    public static int WARNING = 4;
    public static int SEVERE = 5;

    public static int VERBOSE = FINEST;
    public static int DEBUG = FINEST;

}