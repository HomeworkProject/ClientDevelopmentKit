package de.mlessmann.api.networking;

import de.mlessmann.internals.networking.requests.RequestMgr;
import org.json.JSONObject;

/**
 * Created by Life4YourGames on 08.08.16.
 */
public interface IMessageListener {

    void onMessage(JSONObject msg);

    void reportMgr(RequestMgr mgr);

}
