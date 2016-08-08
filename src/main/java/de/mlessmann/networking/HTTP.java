package de.mlessmann.networking;

import com.sun.istack.internal.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by Life4YourGames on 08.08.16.
 */
public class HTTP {

    public static String GET(String sUrl, @Nullable Proxy proxy) throws MalformedURLException, IOException {

        StringBuilder result = new StringBuilder();

        URL url = new URL(sUrl);

        HttpURLConnection connection;

            if (proxy != null)
                connection = (HttpURLConnection) url.openConnection(proxy);
            else
                connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String ln;
        while ((ln = reader.readLine()) != null) {
            result.append(ln);
            if (!ln.endsWith("\n"))
                result.append( "\n");
        }

        reader.close();

        return result.toString();

    }

}
