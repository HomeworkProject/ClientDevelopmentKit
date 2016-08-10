package de.mlessmann.api.data;

/**
 * Created by Life4YourGames on 09.08.16.
 */
public interface IHWUser {

    public static int UNKNOWN = -1;
    public static int NOTFOUNDERR = 404;
    public static int INVALIDCREDERR = 401;
    public static int LOGGEDIN = 200;

    String group();

    String name();

    int loginStatus();



}
