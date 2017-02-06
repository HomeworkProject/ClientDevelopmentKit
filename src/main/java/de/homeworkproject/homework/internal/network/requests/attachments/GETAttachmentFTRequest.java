package de.homeworkproject.homework.internal.network.requests.attachments;

import de.homeworkproject.homework.api.error.Error;
import de.homeworkproject.homework.api.filetransfer.IFTToken;
import de.homeworkproject.homework.api.future.IHWFuture;
import de.homeworkproject.homework.api.homework.IHomeworkAttachment;
import de.homeworkproject.homework.api.logging.ILogLevel;
import de.homeworkproject.homework.api.stream.IHWStreamAcceptor;
import de.homeworkproject.homework.api.stream.IHWStreamReadResult;
import de.homeworkproject.homework.internal.CDKConnectionBase;
import de.homeworkproject.homework.internal.future.HWFuture;
import de.homeworkproject.homework.internal.logging.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.Charset;

/**
 * Created by Life4YourGames on 18.11.16.
 * Downloads attachments from server or web
 */
public class GETAttachmentFTRequest implements Runnable {

    private LogManager r = null;
    private IHomeworkAttachment location;

    private CDKConnectionBase conn;

    private HWFuture<IHWStreamReadResult> future;

    private IFTToken token = null;
    private IHWStreamAcceptor acceptor;
    private Socket sock = null;

    public GETAttachmentFTRequest(LogManager r, IHomeworkAttachment location, CDKConnectionBase conn) {
        this.r = r;
        this.location = location;
        this.future = new HWFuture<IHWStreamReadResult>();
        this.conn = conn;
    }

    public IHWFuture<IHWStreamReadResult> getFuture() {
        return future;
    }

    //--- --- --- --- --- --- --- Execution

    public void setToken(IFTToken token) {
        this.token = token;
    }

    public void setAcceptor(IHWStreamAcceptor acceptor) {
        this.acceptor = acceptor;
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
            fromServer();
        } else if (location.getLocType() == IHomeworkAttachment.LocationType.WEB) {
            fromWeb();
        } else {
            //Invalid location
            future.setError(Error.of(Error.ErrorCode.BADREQUEST));
        }
        future.pokeListeners();
    }

    private void fromServer() {
        sock = new Socket();
        SocketAddress addr = new InetSocketAddress(conn.getHost(), token.getPort());
        try {
            sock.connect(addr, 4000);

            //Send token
            OutputStream out = sock.getOutputStream();
            out.write(token.getToken().getBytes(Charset.forName("utf-8")));

            //Receive file
            InputStream in = sock.getInputStream();
            byte[] buffer = new byte[2048];
            int total = 0;
            int read = 0;
            while ((read = in.read(buffer)) > -1) {
                total += read;
                acceptor.write(buffer, 0, read);
            }
            in.close();
            final int byteTotal = total;
            final int bufferSize = buffer.length;
            future.setPayload(new IHWStreamReadResult() {
                @Override
                public IHWStreamAcceptor getUsedAcceptor() {
                    return acceptor;
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
            future.setError(Error.of(Error.ErrorCode.UNKNOWN));
            return;
        }
       future.setError(Error.OK);
    }

    private void fromWeb() {
        URL u;
        try {
            u = new URL(location.getURL());
        } catch (MalformedURLException e) {
            r.sendLog(this, ILogLevel.WARNING, e);
            future.setError(Error.of(Error.ErrorCode.BADREQUEST, e));
            return;
        }

        if (acceptor==null) {
            future.setError(Error.of(Error.ErrorCode.UNKNOWN));
            return;
        }

        URLConnection conn;
        try {
            conn = u.openConnection();
            InputStream in = conn.getInputStream();
            byte[] buffer = new byte[2048];
            int total = 0;
            int read = 0;
            while ((read = in.read(buffer)) >= -1) {
                total += read;
                acceptor.write(buffer, 0, read);
            }
            in.close();
        } catch (IOException e) {
            r.sendLog(this, ILogLevel.WARNING, e);
            future.setError(Error.of(Error.ErrorCode.UNKNOWN, e));
            return;
        }
        future.setError(Error.OK);
    }
}
