package de.mlessmann.internals.data;

import de.mlessmann.api.annotations.NotNull;
import de.mlessmann.api.data.IHWProvider;
import de.mlessmann.api.main.HWMgr;
import org.json.JSONObject;

/**
 * Created by Life4YourGames on 08.08.16.
 *
 * Used to provide information about a HWServer host.
 * @see HWMgr#getAvailableProviders(String)
 */
public class HWProvider implements IHWProvider {

    private JSONObject json;

    public HWProvider(@NotNull JSONObject information) {

        json = information;

    }

    public boolean isValid() {

        try {

            //Nonexistent keys should raise an exception
            json.getString("address");
            json.getString("name");
            json.getInt("port");
            json.getString("postal");
            json.getString("country");
            json.getJSONObject("optional");

        } catch (Exception e) {

            return false;

        }

        return true;

    }

    public String getAddress() {

        return json.getString("address");

    }

    public String getName() {

        return json.getString("name");

    }

    public String getCountry() {

        return json.getString("country");

    }

    public String getPostal() {

        return json.getString("postal");

    }

    public int getPort() {

        return json.getInt("port");

    }

    public boolean isTCPPlaintextEnabled() {

        return json.getJSONObject("optional").optBoolean("tcp_plaintext_enabled", true);

    }

    public boolean isTCPSecureEnabled() {

        //As the hwServer currently lacks native encryption support, this defaults to false;
        return json.getJSONObject("optional").optBoolean("tcp_encrypted_enabled", false);

    }

    public JSONObject getOptions() {

        return json.getJSONObject("optional");

    }

    public JSONObject getJSON() {
        return json;
    }

}
