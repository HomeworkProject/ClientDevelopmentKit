package de.mlessmann.api.main;

import de.mlessmann.api.annotations.API;
import de.mlessmann.api.annotations.Nullable;
import de.mlessmann.api.data.*;
import de.mlessmann.exceptions.StillConnectedException;
import de.mlessmann.internals.data.HWProvider;
import de.mlessmann.internals.networking.requests.addhw.RequestAddHW;
import de.mlessmann.internals.networking.requests.delhw.RequestDelHW;
import de.mlessmann.internals.networking.requests.gethw.RequestGetHW;
import de.mlessmann.util.HTTP;
import de.mlessmann.internals.networking.requests.RequestMgr;
import de.mlessmann.internals.networking.requests.login.RequestLogin;
import de.mlessmann.internals.networking.requests.version.RequestVersion;
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

    //--------- Authentication ---------

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
    public List<IHWProvider> getAvailableProviders(@Nullable String sUrl) throws JSONException, MalformedURLException, IOException {

        List<JSONObject> providerList = getAvailableProviderList(sUrl);

        ArrayList<IHWProvider> result = new ArrayList<IHWProvider>();

        for (JSONObject o : providerList) {

            IHWProvider p = new HWProvider(o);

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
            reqThread.start();

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
    public IHWFuture<Boolean> isCompatible() {

        RequestVersion req = new RequestVersion();

        req.reportMgr(reqMgr);
        reqMgr.queueRequest(req);

        return req.getFuture();

    }

    @API(APILevel = 1)
    public IHWFuture<IHWUser> login(String grp, String usr, String auth) {

        RequestLogin req = new RequestLogin();

        req.reportMgr(reqMgr);
        req.setGrp(grp);
        req.setUsr(usr);
        req.setAuth(auth);

        reqMgr.queueRequest(req);

        return req.getFuture();

    }

    @API(APILevel = 1)
    public IHWFuture<List<IHWObj>> getHWOn(int yyyy, int MM, int dd) {

        RequestGetHW req = new RequestGetHW();

        req.reportMgr(reqMgr);
        req.setDate(yyyy, MM, dd);

        reqMgr.queueRequest(req);

        return req.getFuture();

    }

    @API(APILevel = 1)
    public IHWFuture<List<IHWObj>> getHWBetween(int yyyyFrom, int MMFrom, int ddFrom, int yyyyTo, int MMTo, int ddTo) {

        RequestGetHW req = new RequestGetHW();

        req.reportMgr(reqMgr);
        req.setDates(yyyyFrom, MMFrom, ddFrom, yyyyTo, MMTo, ddTo);

        reqMgr.queueRequest(req);

        return req.getFuture();


    }

    @API(APILevel = 1)
    public IHWFuture<Boolean> addHW(IHWCarrier hw) {

        RequestAddHW req = new RequestAddHW();

        req.setHW(hw);
        req.reportMgr(reqMgr);
        reqMgr.queueRequest(req);

        return req.getFuture();

    }

    @API(APILevel = 1)
    public IHWFuture<Boolean> delHW(IHWCarrier hw) {

        JSONArray dt = hw.getJSON().getJSONArray("date");
        String id = hw.getJSON().optString("id", "null");

        return delHW(id, dt.optInt(0, 2000), dt.optInt(1, 1), dt.optInt(2,1));

    }

    //==================================================================================================================
    //======================================== API Level 2 =============================================================
    //==================================================================================================================

    //-------------------------------- Provider discovery --------------------------------------------------------------

    @API(APILevel = 2)
    public List<JSONObject> getAvailableProviderList(@Nullable String sUrl) throws JSONException, MalformedURLException, IOException {

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

    @API(APILevel = 2)
    public IHWFuture<Boolean> delHW(String id, int yyyy, int MM, int dd) {

        RequestDelHW req = new RequestDelHW();

        req.setID(id);
        req.setDate(yyyy, MM, dd);

        req.reportMgr(reqMgr);
        reqMgr.queueRequest(req);

        return req.getFuture();

    }

}
