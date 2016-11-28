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
import java.net.*;
import java.nio.charset.Charset;

/**
 * Created by Life4YourGames on 18.11.16.
 * Downloads attachments from server or web
 */
public class DownloadAttachmentFTRequest implements Runnable, IHWFutureProvider<IHWStreamReadResult> {

    private LMgr r = null;
    private IHWAttachmentLocation location;

    private RequestMgr requestMgr;
    private HWMgr hwMgr;

    private HWFuture<IHWStreamReadResult> future;
    private IHWStreamReadResult payload;
    private Object error = null;
    private int errorCode = 0;

    private IFTToken token = null;
    private IHWStreamAcceptor acceptor;
    private Socket sock = null;

    public DownloadAttachmentFTRequest(LMgr r, IHWAttachmentLocation location, RequestMgr reqMgr, HWMgr hwMgr) {
        this.r = r;
        this.location = location;
        this.future = new HWFuture<IHWStreamReadResult>(this);
        this.payload = null;
        this.requestMgr = reqMgr;
        this.hwMgr = hwMgr;
    }

    public IHWFuture<IHWStreamReadResult> getFuture() {
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
    public IHWStreamReadResult getPayload(IFuture<?> future) {
        if (this.future == future)
            return payload;
        else
            return null;
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
            errorCode = IHWFuture.ERRORCodes.PROTOError;
        }

        if (location.getLocType() == IHWAttachmentLocation.LocationType.SERVER) {
            fromServer();
        } else if (location.getLocType() == IHWAttachmentLocation.LocationType.WEB) {
            fromWeb();
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
            out.write(token.getToken().getBytes(Charset.forName("utf-8")));

            //Receive file
            InputStream in = sock.getInputStream();
            byte[] buffer = new byte[2048];
            int total = 0;
            int read = 0;
            while ((read = in.read(buffer)) >= -1) {
                total += read;
                acceptor.write(buffer, 0, read);
            }
            in.close();
        } catch (IOException e) {
            r.sendLog(this, LogLevel.WARNING, e);
            errorCode = IHWFuture.ERRORCodes.UNKNOWN;
            return;
        }
        errorCode = IHWFuture.ERRORCodes.OK;
    }

    private void fromWeb() {
        URL u;
        try {
            u = new URL(location.getURL());
        } catch (MalformedURLException e) {
            r.sendLog(this, LogLevel.WARNING, e);
            errorCode = IHWFuture.ERRORCodes.MALFORMEDURL;
            return;
        }

        if (acceptor==null) {
            errorCode = IHWFuture.ERRORCodes.UNKNOWN;
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
            r.sendLog(this, LogLevel.WARNING, e);
            errorCode = IHWFuture.ERRORCodes.UNKNOWN;
            return;
        }
        errorCode = IHWFuture.ERRORCodes.OK;
    }
}
