package de.mlessmann.api.data;

import org.json.JSONObject;

/**
 * Created by Life4YourGames on 01.09.16.
 */
public interface IHWSession {

    JSONObject getJSON();

    String getToken();

    int[] expires();

    String group();

    String user();

}
