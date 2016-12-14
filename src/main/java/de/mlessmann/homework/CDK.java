package de.mlessmann.homework;

import de.mlessmann.common.annotations.API;
import de.mlessmann.common.annotations.NotNull;
import de.mlessmann.common.annotations.Nullable;
import de.mlessmann.common.annotations.Parallel;
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

    private static CDK INST = null;
    public static CDK getInstance() {
        return INST != null ? INST : new CDK();
    }

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
    @Parallel
    @NotNull
    public IHWFuture<List<IHWProvider>> listProviders(@Nullable String url) {
        ProviderDiscovery disc = new ProviderDiscovery(lMgr, url);
        disc.start();
        return disc.getFuture();
    }
}
