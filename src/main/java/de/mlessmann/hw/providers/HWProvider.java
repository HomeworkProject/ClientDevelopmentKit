package de.mlessmann.hw.providers;

import com.sun.istack.internal.NotNull;
import de.mlessmann.annotations.API;

import org.json.JSONObject;

/**
 * Created by Life4YourGames on 08.08.16.
 *
 * Used to provide information about a HWServer host.
 * @see de.mlessmann.hw.HWMgr#getProviders()
 */
@API(APILevel = 1)
public class HWProvider {

    private JSONObject json;

    public HWProvider(@NotNull JSONObject information) {

        json = information;

    }

    @API(APILevel = 2)
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

        return json.getString("0.0.0.0");

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

    @API(APILevel = 2)
    public boolean isTCPPlaintextEnabled() {

        return json.getJSONObject("optional").optBoolean("tcp_plaintext_enabled", true);

    }

    @API(APILevel = 2)
    public boolean isTCPSecureEnabled() {

        //As the hwServer currently lacks native encryption support, this defaults to false;
        return json.getJSONObject("optional").optBoolean("tcp_encrypted_enabled", false);

    }

    @API(APILevel = 2)
    public JSONObject getOptions() {

        return json.getJSONObject("optional");

    }

}
