package de.mlessmann.homework.api;

import de.mlessmann.common.annotations.*;
import de.mlessmann.homework.api.future.IHWFuture;
import de.mlessmann.homework.api.provider.IHWProvider;
import de.mlessmann.homework.internal.logging.LogManager;
import de.mlessmann.homework.internal.providers.network.ProviderDiscovery;
import de.mlessmann.logging.ILogReceiver;

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

    private ILogReceiver log;
    private LogManager lMgr;

    private CDK() {
        lMgr = new LogManager();
    }

    private CDK setReceiver(ILogReceiver receiver) {
        this.log = receiver;
        return this;
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
    @Parallel
    @NotNull
    public IHWFuture<ICDKConnection> connect(IHWProvider provider) {

    }
}
