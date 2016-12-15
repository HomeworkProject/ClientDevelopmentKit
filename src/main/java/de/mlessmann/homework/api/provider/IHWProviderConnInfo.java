package de.mlessmann.homework.api.provider;

import java.net.SocketAddress;

/**
 * Created by Life4YourGames on 15.12.16.
 */
public interface IHWProviderConnInfo {

    SocketAddress getAddress();

    int getPort();

    int getSSLPort();
}
