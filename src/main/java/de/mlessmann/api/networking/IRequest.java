package de.mlessmann.api.networking;

import org.json.JSONObject;

/**
 * Created by Life4YourGames on 08.08.16.
 */
public interface IRequest {

    /*
     * Yes, this has to be unique within your environment!
     */
    String getUniqueID();

    JSONObject getRequestMsg();

    IMessageListener getListener();

    boolean locksQueue();

    void poke();

    void reportFail(Exception e);

    void reportCommID(int cid);

}
