package de.mlessmann.api.data;

import org.json.JSONObject;

/**
 * Created by Life4YourGames on 09.08.16.
 */
public interface IHWProvider {

    boolean isValid();

    String getAddress();

    String getName();

    String getCountry();

    String getPostal();

    int getPort();

    boolean isTCPPlaintextEnabled();

    boolean isTCPSecureEnabled();

    JSONObject getOptions();

}
