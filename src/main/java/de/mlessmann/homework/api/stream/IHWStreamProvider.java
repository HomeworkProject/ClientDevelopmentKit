package de.mlessmann.homework.api.stream;

import java.io.IOException;

/**
 * Created by Life4YourGames on 28.11.16.
 * Interface to unify stream operations.
 * Mainly for attachments to allow the client to decide where to store them
 *
 * Direction of data flow: Client -> CDK -> Server
 */
public interface IHWStreamProvider {

    /**
     * Indicates that the CDK is about to start reading from this stream
     */
    void open();


    /**
     * Return new bytes from buffer
     *
     * @param bytes the current buffer: May be of varying length and not be fully filled!
     * @return the actual number of bytes filled into the buffer
     * @see java.io.InputStream#read(byte[], int, int) for reference
     */
    int read(byte[] bytes, int off, int len) throws IOException;

    /**
     * Indicates that the server is done reading from this wrapper:
     * Client has to decide what to do with the underlying stream
     */
    void close();
}