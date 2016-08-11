package de.mlessmann.api.data;

/**
 * Created by Life4YourGames on 09.08.16.
 */
public interface IHWUser {

    String group();

    String name();

    @Deprecated
    int loginStatus();

}
