package de.mlessmann.homework.api.stream;

/**
 * Created by Life4YourGames on 13.11.16.
 */
public interface IHWStreamReadResult {

    IHWStreamAcceptor getUsedAcceptor();

    int getByteCount();

    int getUsedBufferSize();
}
