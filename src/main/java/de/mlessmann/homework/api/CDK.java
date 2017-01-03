package de.mlessmann.homework.api;

import de.mlessmann.common.annotations.*;
import de.mlessmann.homework.api.event.ICDKEvent;
import de.mlessmann.homework.api.event.ICDKListener;
import de.mlessmann.homework.api.future.IHWFuture;
import de.mlessmann.homework.api.provider.IHWProvider;
import de.mlessmann.homework.internal.CDKConnection;
import de.mlessmann.homework.internal.logging.LogManager;
import de.mlessmann.homework.internal.providers.network.ProviderDiscovery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Life4YourGames on 14.12.16.
 */
public class CDK {

    //#### ### ### ### ### INITIALIZER ### ### ### ### ### ###
    public static CDK getInstance() {
        return INST != null ? INST : new CDK();
    }
    //#### ### ### ### ### INITIALIZER ### ### ### ### ### ###

    private static CDK INST = null;

    private List<ICDKListener> listeners;
    private LogManager lMgr;


    private CDK() {
        listeners = new ArrayList<ICDKListener>();
        lMgr = new LogManager(this);
    }

    //=== === === === === === === === === === === === === === === === === === === === === === === === === === === ===

    @API
    @NoLogin
    @Parallel
    @NotNull
    public IHWFuture<List<IHWProvider>> listProviders(@Nullable String url) {
        ProviderDiscovery disc = new ProviderDiscovery(lMgr, url);
        disc.start();
        return disc.getFuture();
    }

    @API
    @NoLogin
    @NotNull
    public ICDKConnection connect(IHWProvider provider) {
        return new CDKConnection(this, provider);
    }

    //=== === === === === === === === === === === === === === === === === === === === === === === === === === === ===

    @API(APILevel = 2)
    public void registerListener(@NotNull ICDKListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    @API(APILevel = 2)
    public void unregisterListener(@NotNull ICDKListener listener) {
        if (listener!=null && listeners.contains(listener))
            listeners.remove(listener);
    }

    //=== === === === === === === === === === === === === === === === === === === === === === === === === === === ===

    public void fireEvent(ICDKEvent event) {
        for (int i = listeners.size(); i >0; i--)
            listeners.get(i).onEvent(event);
    }
}
