package de.mlessmann.api.networking;

import de.mlessmann.internals.networking.requests.RequestMgr;
import org.json.JSONObject;

/**
 * Created by Life4YourGames on 08.08.16.
 */
public interface IMessageListener {

    /**
     * If the message has been handled by this listener return true;
     * @return Whether this Listener processed some information from the message
     */
    boolean onMessage(JSONObject msg);

    void reportMgr(RequestMgr mgr);

    void onClosed(CloseReason rsn);
}
