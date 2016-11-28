package de.mlessmann.api.data;

/**
 * Created by Life4YourGames on 13.11.16.
 */
public interface IHWStreamReadResult {

    IHWStreamAcceptor getUsedAcceptor();

    int getByteCount();

    int getUsedBufferSize();
}
