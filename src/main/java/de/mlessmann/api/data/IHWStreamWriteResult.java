package de.mlessmann.api.data;

/**
 * Created by Life4YourGames on 29.11.16.
 */
public interface IHWStreamWriteResult {

    IHWStreamProvider getUsedProvider();

    int getByteCount();

    int getUsedBufferSize();
}
