package de.mlessmann.internals.networking.requests.providers;

import de.mlessmann.api.data.IHWFuture;
import de.mlessmann.api.data.IHWProvider;
import de.mlessmann.api.logging.LogLevel;
import de.mlessmann.common.annotations.Nullable;
import de.mlessmann.internals.data.HWProvider;
import de.mlessmann.internals.logging.LMgr;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import static de.mlessmann.common.HTTP.GET;

/**
 * Created by Life4YourGames on 02.09.16.
 */
public class ProviderDiscovery implements Runnable {

    private FutureProvider<List<JSONObject>> jsonProvider;
    private FutureProvider<List<IHWProvider>> objProvider;

    private String sUrl;
    private Proxy proxy;
    private boolean running = false;
    private Thread thread = null;
    private LMgr lmgr;

    public ProviderDiscovery(LMgr lmgr) {
        jsonProvider = new FutureProvider<List<JSONObject>>();
        objProvider = new FutureProvider<List<IHWProvider>>();
        this.lmgr = lmgr;
    }

    @Override
    public void run() {
        try {
            if (sUrl == null)
                sUrl = "http://dev.m-lessmann.de/hwserver/providerDiscovery.json";
            String res = GET(sUrl, proxy);

            JSONObject resp = new JSONObject(res);
            JSONArray serverList = resp.getJSONArray("servers");

            //TODO: Reminder for nameServers
            ArrayList<JSONObject> jsonObjects = new ArrayList<JSONObject>();
            //For-Each not possible due to the org.json lib in android
            for (int i = 0; i < serverList.length(); i++) {
                Object o = serverList.get(i);
                if (o instanceof JSONObject)
                    jsonObjects.add((JSONObject) o);
            }
            jsonProvider.setPayload(jsonObjects);
            jsonProvider.setErrorCode(IHWFuture.ERRORCodes.OK);


            ArrayList<IHWProvider> providers = new ArrayList<IHWProvider>();
            for (JSONObject o : jsonObjects) {

                IHWProvider p = new HWProvider(o);

                if (p.isValid())
                    providers.add(p);

            }
            objProvider.setPayload(providers);
            objProvider.setErrorCode(IHWFuture.ERRORCodes.OK);

            objProvider.pokeListeners();
            jsonProvider.pokeListeners();

        } catch (Exception e) {
            jsonProvider.setError(e);
            jsonProvider.setErrorCode(IHWFuture.ERRORCodes.UNKNOWN);
            objProvider.setError(e);
            objProvider.setErrorCode(IHWFuture.ERRORCodes.UNKNOWN);
            lmgr.sendLog(this, LogLevel.SEVERE, "Unable to perform ProviderDiscovery: " + e.toString());
            lmgr.sendLog(this, LogLevel.SEVERE, e);
        }
        running = false;
        thread = null;
    }

    //-------------------------------- Future --------------------------------------------------------------------------

    public IHWFuture<List<JSONObject>> getJSONFuture() {
        return jsonProvider.getFuture();
    }

    public IHWFuture<List<IHWProvider>> getObjFuture() {
        return objProvider.getFuture();
    }

    //-------------------------------- Start ---------------------------------------------------------------------------

    public void requestStart(@Nullable String sUrl) {
        if (!running) {
            this.sUrl = sUrl;
            thread = new Thread(this);
            thread.start();
        }
    }

}
