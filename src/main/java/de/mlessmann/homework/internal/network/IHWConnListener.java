package de.mlessmann.homework.internal.network;

import de.mlessmann.homework.api.event.network.CloseReason;
import org.json.JSONObject;

/**
 * Created by Life4YourGames on 03.01.17.
 */
public interface IHWConnListener {

    boolean processJSON(JSONObject o);

    void onClosed(CloseReason rsn);
}
