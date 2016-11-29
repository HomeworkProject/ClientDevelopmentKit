package de.mlessmann.internals.networking.requests.attachments;

import de.mlessmann.api.data.*;
import de.mlessmann.api.logging.LogLevel;
import de.mlessmann.api.main.HWMgr;
import de.mlessmann.common.parallel.IFuture;
import de.mlessmann.internals.data.HWFuture;
import de.mlessmann.internals.logging.LMgr;
import de.mlessmann.internals.networking.requests.RequestMgr;

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
public class PushAttachmentFTRequest implements Runnable, IHWFutureProvider<IHWStreamWriteResult> {

    private LMgr r = null;
    private IHWAttachmentLocation location;

    private RequestMgr requestMgr;
    private HWMgr hwMgr;

    private HWFuture<IHWStreamWriteResult> future;
    private IHWStreamWriteResult payload;
    private Object error = null;
    private int errorCode = 0;

    private IFTToken token = null;
    private IHWStreamProvider provider;
    private Socket sock = null;

    public PushAttachmentFTRequest(LMgr r, IHWAttachmentLocation location, RequestMgr reqMgr, HWMgr hwMgr) {
        this.r = r;
        this.location = location;
        this.future = new HWFuture<IHWStreamWriteResult>(this);
        this.payload = null;
        this.requestMgr = reqMgr;
        this.hwMgr = hwMgr;
    }

    public IHWFuture<IHWStreamWriteResult> getFuture() {
        return future;
    }

    @Override
    public Object getError(IHWFuture<?> future) {
        if (future == this.future)
            return error;
        else
            return null;
    }

    @Override
    public int getErrorCode(IHWFuture<?> future) {
        if (this.future == future)
            return errorCode;
        else
            return 0;
    }

    @Override
    public IHWStreamWriteResult getPayload(IFuture<?> future) {
        if (this.future == future)
            return payload;
        else
            return null;
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
        payload = null;
        errorCode = 0;
        if (token == null) {
            //No token has previously been set
            errorCode = IHWFuture.ERRORCodes.PROTOError;
        }

        if (location.getLocType() == IHWAttachmentLocation.LocationType.SERVER) {
            fromServer();
        } else {
            //Invalid location
            errorCode = IHWFuture.ERRORCodes.PROTOError;
        }
        future.pokeListeners();
    }

    private void fromServer() {
        sock = new Socket();
        SocketAddress addr = new InetSocketAddress(hwMgr.getServerAddressString(), token.getPort());
        try {
            sock.connect(addr, 4000);

            //Send token
            OutputStream out = sock.getOutputStream();
            InputStream in = sock.getInputStream();
            out.write(token.getToken().getBytes(Charset.forName("utf-8")));

            //Send file
            byte[] buffer = new byte[2048];
            int total = 0;
            int read = 0;
            while ((read = provider.read(buffer, 0, 2048)) >= -1) {
                total += read;
                out.write(buffer, 0, read);
            }
            out.close();
            final int byteTotal = total;
            final int bufferSize = buffer.length;
            payload = new IHWStreamWriteResult() {
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
            };
            buffer[0] = 0;
            in.read(buffer, 0, 1);
            if (buffer[0] != 1) {
                //TODO: Implement some form of feedback
            }

        } catch (IOException e) {
            r.sendLog(this, LogLevel.WARNING, e);
            errorCode = IHWFuture.ERRORCodes.UNKNOWN;
            return;
        }
        errorCode = IHWFuture.ERRORCodes.OK;
    }

}
