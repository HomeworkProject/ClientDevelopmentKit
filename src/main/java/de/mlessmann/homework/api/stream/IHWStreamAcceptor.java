package de.mlessmann.homework.api.stream;

import java.io.IOException;

/**
 * Created by Life4YourGames on 28.11.16.
 * Interface to unify stream operations.
 * Mainly for attachments to allow the client to decide where to store the attachment
 *
 * Direction of data flow: server -> CDK -> Client
 */
public interface IHWStreamAcceptor {

    /**
     * Indicates that the CDK is about to start writing to this stream
     */
    void open();

    /**
     * Accept new bytes from buffer
     * @param bytes the current buffer: May be of varying length and not be fully filled!
     * @see java.io.OutputStream#write(byte[], int, int) for reference
     */
    void write(byte[] bytes, int off, int len) throws IOException;

    /**
     * Indicates that the CDK is done writing to this wrapper:
     * Client has to decide what to do with the underlying stream
     */
    void close();
}