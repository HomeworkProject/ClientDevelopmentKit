package de.mlessmann.networking.requests;

import org.json.JSONObject;

/**
 * Created by Life4YourGames on 08.08.16.
 */
public interface IMessageListener {

    void onMessage(JSONObject msg);

    void reportMgr(RequestMgr mgr);

}
