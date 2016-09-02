package de.mlessmann.internals.networking.requests.providers;

import de.mlessmann.api.data.IHWFuture;
import de.mlessmann.api.data.IHWProvider;
import de.mlessmann.internals.data.HWProvider;
import de.mlessmann.util.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

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

    public ProviderDiscovery() {
        jsonProvider = new FutureProvider<List<JSONObject>>();
        objProvider = new FutureProvider<List<IHWProvider>>();
    }

    @Override
    public void run() {
        try {
            if (sUrl == null)
                sUrl = "http://schule.m-lessmann.de/hwserver/sources.json";
            String res = HTTP.GET(sUrl, proxy);

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
            jsonProvider.setErrorCode(IHWFuture.ERRORCodes.UNKNOWN);
            objProvider.setErrorCode(IHWFuture.ERRORCodes.UNKNOWN);
            //TODO:Implement error handler!
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

    public void requestStart() {
        if (!running) {
            thread = new Thread(this);
            thread.start();
        }
    }

}
