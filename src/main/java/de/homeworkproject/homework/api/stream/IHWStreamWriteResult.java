package de.homeworkproject.homework.api.stream;

/**
 * Created by Life4YourGames on 29.11.16.
 */
public interface IHWStreamWriteResult {

    IHWStreamProvider getUsedProvider();

    int getByteCount();

    int getUsedBufferSize();
}
