package de.mlessmann.api.data;

import java.io.IOException;

/**
 * Created by Life4YourGames on 28.11.16.
 * Interface to unify stream operations.
 * Mainly for attachments to allow the client to decide where to store the attachment
 */
public interface IHWStreamAcceptor {

    /**
     * Accept new bytes from buffer
     * @param bytes the current buffer: May be of varying length and not be fully filled!
     * @see java.io.OutputStream#write(byte[], int, int) for reference
     */
    void write(byte[] bytes, int off, int len) throws IOException;
}
