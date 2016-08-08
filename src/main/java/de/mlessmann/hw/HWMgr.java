package de.mlessmann.hw;

import com.sun.istack.internal.Nullable;

import de.mlessmann.annotations.API;
import de.mlessmann.exceptions.StillConnectedException;
import de.mlessmann.hw.providers.HWProvider;
import de.mlessmann.networking.HTTP;
import de.mlessmann.networking.requests.RequestMgr;
import de.mlessmann.networking.requests.prefactured.version.RequestVersion;
import de.mlessmann.networking.requests.results.HWFuture;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Life4YourGames on 08.08.16.
 * Main part of the API.
 */
@API
public class HWMgr {

    private Proxy proxy;

    private String serverAddress;
    private int port;
    private boolean secTCP;

    //-------- Connection ----------
    private boolean connected;
    private Socket socket;
    private RequestMgr reqMgr;
    private Thread reqThread;

    //==================================================================================================================
    //======================================== API Level 1 =============================================================
    //==================================================================================================================

    //-------------------------------- Provider discovery --------------------------------------------------------------

    /**
     * Returns a list of listed providers.
     * @param sUrl The sources URL to use, defaults to official sources.
     * @return List\<HWProvider\> of listed providers.
     */
    @API(APILevel = 1)
    public List<HWProvider> getAvailableProviders(@Nullable String sUrl) throws JSONException, MalformedURLException, IOException {

        ArrayList<JSONObject> providerList = getAvailableProviderList(sUrl);

        ArrayList<HWProvider> result = new ArrayList<HWProvider>();

        for (JSONObject o : providerList) {

            HWProvider p = new HWProvider(o);

            if (p.isValid())
                result.add(p);

        }

        return result;

    }

    /**
     * The Mgr will use this provider as server.
     * @param p Provider holding url, port and other information
     * @exception StillConnectedException is thrown when the Mgr is still connected to a provider,
     *            release the connection first.
     */

    @API(APILevel = 1)
    public void setProvider(HWProvider p) throws StillConnectedException {

        setServerAddress(p.getAddress());
        setPort(p.getPort());

    }

    //---------------------------------------- Connection --------------------------------------------------------------

    @API(APILevel = 1)
    public boolean connect() throws StillConnectedException {

        if (connected)
            throw new StillConnectedException("Unable to connect to new server while still connected to old one!");

        try {

            //TODO: Implement encryption/compression/etc.

            InetSocketAddress sAddr = new InetSocketAddress(serverAddress, port);

            socket = new Socket();

            socket.connect(sAddr);

            reqMgr = new RequestMgr(socket);

            reqThread = new Thread(reqMgr);
            reqThread.run();

        } catch (IOException e) {

            //TODO: Implement error reporting
            return false;

        }

        return socket.isConnected();

    }

    @API(APILevel = 1)
    public boolean connect(HWProvider provider) {

        if (connected)
            release(true);

        try {
            setProvider(provider);
            return connect();
        } catch (StillConnectedException e) {

            //TODO: Implement "crash" on unexpected exception
            return false;

        }

    }

    @API(APILevel = 1)
    public void release() {
        release(true);
    }

    @API(APILevel = 2)
    public void release(boolean forced) {
        try {
            if (socket.isConnected()) {
                socket.close();
            }
        } catch (IOException e) {

            //Just abbandon socket
            //TODO: Implement error reporting

        }

        reqMgr.kill();

        if (!socket.isConnected() || forced)
            connected = false;

    }

    //---------------------------------------- Communication -----------------------------------------------------------

    @API(APILevel = 1)
    public HWFuture<Boolean> isCompatible() {

        RequestVersion req = new RequestVersion();

        req.reportMgr(reqMgr);
        reqMgr.queueRequest(req);

        return req.getFuture();

    }

    //==================================================================================================================
    //======================================== API Level 2 =============================================================
    //==================================================================================================================

    //-------------------------------- Provider discovery --------------------------------------------------------------

    @API(APILevel = 2)
    public ArrayList<JSONObject> getAvailableProviderList(@Nullable String sUrl) throws JSONException, MalformedURLException, IOException {

        if (sUrl == null)
            sUrl = "http://schule.m-lessmann.de/hwserver/sources.json";

        String res = HTTP.GET(sUrl, proxy);

        JSONObject resp = new JSONObject(res);

        JSONArray serverList = resp.getJSONArray("servers");

        //TODO: Reminder for nameServers

        ArrayList<JSONObject> result = new ArrayList<JSONObject>();

        for (Object o : serverList) {

            if (o instanceof JSONObject)
                result.add((JSONObject) o);

        }

        return result;

    }

    @API(APILevel = 2)
    public void setServerAddress(String serverAddress) throws StillConnectedException {
        if (connected)
            throw new StillConnectedException("Cannot change address when still connected!");
        this.serverAddress = serverAddress;
    }

    @API(APILevel = 2)
    public void setPort(int port) throws StillConnectedException {
        if (connected)
            throw new StillConnectedException("Cannot change port when still connected!");
        this.port = port;
    }

}
