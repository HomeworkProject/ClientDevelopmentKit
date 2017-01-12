package de.mlessmann.homework.internal.providers.network;

import de.mlessmann.common.annotations.Nullable;
import de.mlessmann.homework.api.error.Error;
import de.mlessmann.homework.api.future.IHWFuture;
import de.mlessmann.homework.api.logging.ILogLevel;
import de.mlessmann.homework.api.provider.IHWProvider;
import de.mlessmann.homework.internal.future.HWFuture;
import de.mlessmann.homework.internal.logging.LogManager;
import de.mlessmann.homework.internal.providers.HWProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static de.mlessmann.common.HTTP.GET;

/**
 * Created by Life4YourGames on 14.12.16.
 */
public class ProviderDiscovery extends Thread {

    public static final String defURL = "https://dev.m-lessmann.de/hwserver/providerDiscovery/0.2/sources.json";

    private LogManager lmgr;
    private String sUrl;
    private IHWFuture<List<IHWProvider>> future;

    public ProviderDiscovery(LogManager lmgr, @Nullable String url) {
        this.lmgr = lmgr;
        this.sUrl = url!=null ? url : defURL;
        this.future = new HWFuture<List<IHWProvider>>();
    }

    public IHWFuture<List<IHWProvider>> getFuture() {
        return future;
    }

    @Override
    public void run() {
        try {
            //Make sure that there's time to register the future-listeners
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            //Continue
        }

        try {
            String res = GET(sUrl, null);

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

            ArrayList<IHWProvider> providers = new ArrayList<IHWProvider>();
            for (JSONObject o : jsonObjects) {
                HWProvider p = new HWProvider(o);
                if (p.isValid())
                    providers.add(p);
            }

            ((HWFuture) future).setPayload(providers);
            ((HWFuture) future).setError(Error.OK);
            ((HWFuture) future).pokeListeners();

        } catch (Exception e) {
            ((HWFuture) future).setError(Error.of(Error.ErrorCode.UNKNOWN, e));
            lmgr.sendLog(this, ILogLevel.SEVERE, "Unable to perform ProviderDiscovery: " + e.toString());
            lmgr.sendLog(this, ILogLevel.SEVERE, e);
            ((HWFuture) future).pokeListeners();
        }
    }

}
