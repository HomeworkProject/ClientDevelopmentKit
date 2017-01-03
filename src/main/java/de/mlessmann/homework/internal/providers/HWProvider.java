package de.mlessmann.homework.internal.providers;

import de.mlessmann.common.annotations.NotNull;
import de.mlessmann.homework.api.CDK;
import de.mlessmann.homework.api.provider.IHWProvider;
import de.mlessmann.homework.api.provider.IHWProviderConnInfo;
import org.json.JSONObject;

/**
 * Created by Life4YourGames on 08.08.16.
 *
 * Used to provide information about a HWServer host.
 * @see CDK#listProviders(String)
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
            json.getString("postal");
            json.getString("country");
            json.getString("state");
            json.getString("city");
            json.getJSONObject("optional");

            JSONObject conn = json.getJSONObject("connection");
            conn.getString("host");
            conn.getInt("ssl");
            conn.getInt("plain");
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

    public String getState() {
        return json.getString("state");
    }

    public String getCity() {
        return json.getString("city");
    }

    public String getPostal() {
        return json.getString("postal");
    }

    public JSONObject getJSON() {
        return json;
    }

    @Override
    public IHWProviderConnInfo getConnInfo() {
        JSONObject conn = json.getJSONObject("connection");
        final String host = conn.getString("host");
        final int sslPort = conn.getInt("ssl");
        final int port = conn.getInt("plain");
        return new IHWProviderConnInfo() {
            @Override
            public String getHost() {
                return host;
            }

            @Override
            public int getPort() {
                return port;
            }

            @Override
            public int getSSLPort() {
                return sslPort;
            }
        };
    }
}