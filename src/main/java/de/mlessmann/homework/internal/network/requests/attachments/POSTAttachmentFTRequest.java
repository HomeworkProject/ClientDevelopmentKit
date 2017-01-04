package de.mlessmann.homework.internal.network.requests.attachments;

import de.mlessmann.homework.api.error.Error;
import de.mlessmann.homework.api.filetransfer.IFTToken;
import de.mlessmann.homework.api.future.IHWFuture;
import de.mlessmann.homework.api.homework.IHomeworkAttachment;
import de.mlessmann.homework.api.logging.ILogLevel;
import de.mlessmann.homework.api.stream.IHWStreamProvider;
import de.mlessmann.homework.api.stream.IHWStreamWriteResult;
import de.mlessmann.homework.internal.CDKConnectionBase;
import de.mlessmann.homework.internal.future.HWFuture;
import de.mlessmann.homework.internal.logging.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.Charset;

/**
 * Created by Life4YourGames on 28.11.16.
 */
public class POSTAttachmentFTRequest implements Runnable {

    private LogManager r = null;
    private IHomeworkAttachment location;

    private CDKConnectionBase conn;

    private HWFuture<IHWStreamWriteResult> future;

    private IFTToken token = null;
    private IHWStreamProvider provider;
    private Socket sock = null;

    public POSTAttachmentFTRequest(LogManager r, IHomeworkAttachment location, CDKConnectionBase conn) {
        this.r = r;
        this.location = location;
        this.future = new HWFuture<IHWStreamWriteResult>();
        this.conn = conn;
    }

    public IHWFuture<IHWStreamWriteResult> getFuture() {
        return future;
    }

    //--- --- --- --- --- --- --- Execution

    public void setToken(IFTToken token) {
        this.token = token;
    }

    public void setProvider(IHWStreamProvider provider) {
        this.provider = provider;
    }

    @Override
    public void run() {
        if (token == null) {
            //No token has previously been set
            future.setError(Error.of(Error.ErrorCode.BADREQUEST));
            future.pokeListeners();
            return;
        }

        if (location.getLocType() == IHomeworkAttachment.LocationType.SERVER) {
            toServer();
        } else {
            //Invalid location
            future.setError(Error.of(Error.ErrorCode.BADREQUEST));
        }
        future.pokeListeners();
    }

    private void toServer() {
        sock = new Socket();
        SocketAddress addr = new InetSocketAddress(conn.getHost(), token.getPort());
        try {
            sock.connect(addr, 4000);

            //Send token
            OutputStream out = sock.getOutputStream();
            InputStream in = sock.getInputStream();
            out.write(token.getToken().getBytes(Charset.forName("utf-8")));
            out.flush();

            //Send file
            byte[] buffer = new byte[2048];
            int total = 0;
            int read = 0;
            while ((read = provider.read(buffer, 0, 2048)) > -1) {
                total += read;
                out.write(buffer, 0, read);
                out.flush();
            }
            sock.close();
            final int byteTotal = total;
            final int bufferSize = buffer.length;
            future.setPayload(new IHWStreamWriteResult() {
                @Override
                public IHWStreamProvider getUsedProvider() {
                    return provider;
                }

                @Override
                public int getByteCount() {
                    return byteTotal;
                }

                @Override
                public int getUsedBufferSize() {
                    return bufferSize;
                }
            });
        } catch (IOException e) {
            r.sendLog(this, ILogLevel.WARNING, e);
            future.setError(Error.of(Error.ErrorCode.UNKNOWN, e));
            return;
        }
        future.setError(Error.OK);
    }

}
