package de.mlessmann.api.data;

import org.json.JSONObject;

/**
 * Created by Life4YourGames on 09.08.16.
 */
public interface IHWProvider {

    boolean isValid();

    String getAddress();

    int getPort();

    /**
     * Name of school
     */
    String getName();

    /**
     * Country-Code, e.g. "DE"
     */
    String getCountry();

    /**
     * Equals State/Province
     */
    String getState();

    /**
     * Postal code
     */
    String getPostal();

    boolean isTCPPlaintextEnabled();

    boolean isTCPSecureEnabled();

    JSONObject getOptions();

    JSONObject getJSON();

}
