package de.mlessmann.homework.internal;

import de.mlessmann.homework.api.CDK;
import de.mlessmann.homework.api.provider.IHWProvider;
import de.mlessmann.homework.api.provider.IHWProviderConnInfo;

import java.net.SocketAddress;

/**
 * Created by Life4YourGames on 15.12.16.
 */
public class CDKConnectionBase extends Thread {

    private IHWProvider provider;
    private SocketAddress address = null;
    private int port = 0;
    private int sslPort = 0;

    public CDKConnectionBase(CDK cdk) {

    }

    public void setProvider(IHWProvider provider) {
        this.provider = provider;
        IHWProviderConnInfo ci = provider.getConnInfo();
        setProvider(ci.getAddress(), ci.getPort(), ci.getSSLPort());
    }

    public void setProvider(SocketAddress addr, int port, int sslPort) {
        this.address = addr;
        this.port = port;
        this.sslPort = sslPort;
    }

    @Override
    public void run() {

    }
}
