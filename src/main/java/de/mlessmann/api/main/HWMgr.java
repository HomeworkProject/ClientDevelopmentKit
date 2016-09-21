package de.mlessmann.api.main;

import de.mlessmann.api.data.*;
import de.mlessmann.api.logging.ILogListener;
import de.mlessmann.common.HTTP;
import de.mlessmann.common.annotations.API;
import de.mlessmann.common.annotations.Nullable;
import de.mlessmann.exceptions.StillConnectedException;
import de.mlessmann.internals.data.HWProvider;
import de.mlessmann.internals.logging.LMgr;
import de.mlessmann.internals.networking.requests.RequestMgr;
import de.mlessmann.internals.networking.requests.addhw.RequestAddHW;
import de.mlessmann.internals.networking.requests.delhw.RequestDelHW;
import de.mlessmann.internals.networking.requests.gethw.RequestGetHW;
import de.mlessmann.internals.networking.requests.list.RequestList;
import de.mlessmann.internals.networking.requests.login.RequestLogin;
import de.mlessmann.internals.networking.requests.providers.ProviderDiscovery;
import de.mlessmann.internals.networking.requests.version.RequestVersion;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Proxy;
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

    //ProviderDiscovery
    private ProviderDiscovery providerDiscovery;

    //-------- Connection ----------
    private boolean connected;
    private RequestMgr reqMgr;
    private Thread reqThread;

    //-------- Errors + Logging ----
    private LMgr lMgr = new LMgr();

    //--------- Authentication ---------

    //==================================================================================================================
    //======================================== API Level 1 =============================================================
    //==================================================================================================================

    //-------------------------------- Provider discovery --------------------------------------------------------------

    /**
     * Returns a list of listed providers.
     * @param sUrl The sources URL to use, defaults to official sources.
     * @return List\<HWProvider\> of listed providers.
     * @deprecated Will be replaced by a multi-threaded method that will have similar signature
     *             Will change 'throws' signature! LMgr integration impending.
     *             This method will be removed on first major CDK release.
     *             [REPLACED BY:]
     *             @see #getAvailableProvidersOBJ(String)
     */
    @Deprecated
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

    @API(APILevel = 1)
    public IHWFuture<List<IHWProvider>> getAvailableProvidersOBJ(@Nullable String sUrl) {
        getAvailableProvidersJSON(sUrl);
        return providerDiscovery.getObjFuture();
    }

    /**
     * The Mgr will use this provider as server.
     * @param p Provider holding url, port and other information
     * @exception StillConnectedException is thrown when the Mgr is still connected to a provider,
     *            release the connection first.
     */

    @API(APILevel = 1)
    public void setProvider(IHWProvider p) throws StillConnectedException {
        setServerAddress(p.getAddress());
        setPort(p.getPort());
    }

    //---------------------------------------- Connection --------------------------------------------------------------

    @API(APILevel = 1)
    public IHWFuture<Exception> connect() throws StillConnectedException {
        if (connected)
            throw new StillConnectedException("Unable to connect to new server while still connected to old one!");
        //TODO: Implement encryption/compression/etc.
        reqMgr = new RequestMgr(lMgr, serverAddress, port);
        reqThread = new Thread(reqMgr);
        reqThread.start();
        return reqMgr.getConnResult();

    }

    @API(APILevel = 1)
    public IHWFuture<Exception> connect(IHWProvider provider) throws StillConnectedException{

        if (connected)
            release(true);

        setProvider(provider);
        return connect();

    }

    @API(APILevel = 1)
    public void release() {
        release(true);
    }

    @API(APILevel = 2)
    public void release(boolean forced) {
        if (reqMgr == null)
            return;

        reqMgr.kill();

        if (isConnected() || forced)
            connected = false;

    }

    @API(APILevel = 1)
    public boolean isConnected() {
        if (reqMgr == null)
            return false;
        return reqMgr.isActive();
    }

    //---------------------------------------- Communication -----------------------------------------------------------

    @API(APILevel = 1)
    public IHWFuture<Boolean> isCompatible() {

        RequestVersion req = new RequestVersion(lMgr);

        req.reportMgr(reqMgr);
        reqMgr.queueRequest(req);

        return req.getFuture();
    }

    @API(APILevel = 1)
    public IHWFuture<IHWGroupMapping> getGroups() {
        RequestList req = new RequestList(lMgr);
        req.reportMgr(reqMgr);
        reqMgr.queueRequest(req);

        return req.getFuture();
    }

    @API(APILevel = 1)
    public IHWFuture<IHWUser> login(String grp, String usr, String auth) {

        RequestLogin req = new RequestLogin(lMgr);

        req.reportMgr(reqMgr);
        req.setGrp(grp);
        req.setUsr(usr);
        req.setAuth(auth);

        reqMgr.queueRequest(req);

        return req.getFuture();
    }

    @API(APILevel = 1)
    public IHWFuture<IHWUser> login(String token) {

        RequestLogin req = new RequestLogin(lMgr);

        req.reportMgr(reqMgr);
        req.setToken(token);

        reqMgr.queueRequest(req);

        return req.getFuture();
    }

    @API(APILevel = 1)
    public IHWFuture<List<IHWObj>> getHWOn(int yyyy, int MM, int dd) {

        RequestGetHW req = new RequestGetHW(lMgr);

        req.reportMgr(reqMgr);
        req.setDate(yyyy, MM, dd);

        reqMgr.queueRequest(req);

        return req.getFuture();
    }

    @API(APILevel = 1)
    public IHWFuture<List<IHWObj>> getHWBetween(int yyyyFrom, int MMFrom, int ddFrom, int yyyyTo, int MMTo, int ddTo) {

        RequestGetHW req = new RequestGetHW(lMgr);

        req.reportMgr(reqMgr);
        req.setDates(yyyyFrom, MMFrom, ddFrom, yyyyTo, MMTo, ddTo);

        reqMgr.queueRequest(req);

        return req.getFuture();
    }

    @API(APILevel = 1)
    public IHWFuture<Boolean> addHW(IHWCarrier hw) {

        RequestAddHW req = new RequestAddHW(lMgr);

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

    //-------------------------------- Logging -------------------------------------------------------------------------

    /**
     * Register a new LogListener for all cdk component messages
     * @param l Listener
     * @see LMgr#registerListener(ILogListener)
     */
    public void registerLogListener(ILogListener l) {
        lMgr.registerListener(l);
    }

    /**
     * Unregister a LogListener from all cdk component messages
     * (Does not unregister from possible sub-components
     * @param l Listener
     * @see LMgr#unregisterListener(ILogListener)
     */
    public void unregisterLogListener(ILogListener l) {
        lMgr.unregisterListener(l);
    }

    //==================================================================================================================
    //======================================== API Level 2 =============================================================
    //==================================================================================================================

    //-------------------------------- Provider discovery --------------------------------------------------------------
    /**
     * @param sUrl
     * @return
     * @throws JSONException
     * @throws MalformedURLException
     * @throws IOException
     * @deprecated Will be replaced by a multi-threaded method that will have similar signature
     *             Will change 'throws' signature! LMgr integration impending.
     *             This method will be removed on first major CDK release.
     *             [REPLACED BY:]
     *             @see #getAvailableProvidersJSON(String)
     */
    @Deprecated
    @API(APILevel = 2)
    public List<JSONObject> getAvailableProviderList(@Nullable String sUrl) throws JSONException, MalformedURLException, IOException {

        if (sUrl == null)
            sUrl = "http://schule.m-lessmann.de/hwserver/sources.json";

        String res = HTTP.GET(sUrl, proxy);

        JSONObject resp = new JSONObject(res);

        JSONArray serverList = resp.getJSONArray("servers");

        //TODO: Reminder for nameServers

        ArrayList<JSONObject> result = new ArrayList<JSONObject>();

        //For-Each not possible due to the org.json lib in android
        for (int i = 0; i < serverList.length(); i++) {
            Object o = serverList.get(i);
            if (o instanceof JSONObject)
                result.add((JSONObject) o);
        }

        return result;

    }

    @API(APILevel = 2)
    public IHWFuture<List<JSONObject>> getAvailableProvidersJSON(@Nullable String sUrl) {

        if (providerDiscovery == null)
            providerDiscovery = new ProviderDiscovery(lMgr);
        providerDiscovery.requestStart(sUrl);
        return providerDiscovery.getJSONFuture();

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
        RequestDelHW req = new RequestDelHW(lMgr);
        req.setID(id);
        req.setDate(yyyy, MM, dd);
        req.reportMgr(reqMgr);
        reqMgr.queueRequest(req);
        return req.getFuture();
    }

}
