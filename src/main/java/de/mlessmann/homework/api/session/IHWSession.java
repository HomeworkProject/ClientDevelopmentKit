package de.mlessmann.homework.api.session;

import org.json.JSONObject;

/**
 * Created by Life4YourGames on 01.09.16.
 */
public interface IHWSession {

    JSONObject getJSON();

    String getToken();

    /**
     * [yyyy, MM, dd, HH, mm]
     * Determines when the token expires
     * Renewal currently not possible
     */
    int[] expires();

    String group();

    String user();
}