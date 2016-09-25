package de.mlessmann.internals.networking.requests;

import de.mlessmann.api.data.IHWFuture;
import de.mlessmann.api.data.IHWFutureProvider;
import de.mlessmann.api.networking.IMessageListener;
import de.mlessmann.api.networking.IRequest;
import de.mlessmann.common.annotations.API;
import de.mlessmann.exceptions.OutOfCIDsException;
import de.mlessmann.internals.data.HWFuture;
import de.mlessmann.internals.logging.LMgr;
import de.mlessmann.internals.networking.requests.greeting.GreetListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.*;

import static de.mlessmann.api.logging.LogLevel.*;

/**
 * Created by Life4YourGames on 08.08.16.
 */
@API(APILevel = 3)
public class RequestMgr implements Runnable, IHWFutureProvider<Exception> {

    private String serverAddress;
    private int port;
    private SocketAddress sAddr;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private IRequest requestsLockedBy = null;

    //TODO: Add an integration to the HWMgrs error handling system
    private boolean crashed;
    private Exception crashRsn;
    private boolean killed = false;
    private LMgr lMgr;

    private List<IMessageListener> listeners = new ArrayList<IMessageListener>();
    private List<IRequest> requestQueue = new ArrayList<IRequest>();
    private Map<IRequest, Integer> cIDs = new HashMap<IRequest, Integer>();
    private Random rnd = new Random();

    //HWFutureProvider
    private HWFuture<Exception> fConnResult;
    private Exception connResult = null;
    private int connErrCode = 0;

    public RequestMgr(LMgr logger, String serverAddr, int port) {

        lMgr = logger;

        this.serverAddress = serverAddr;
        this.port = port;

        fConnResult = new HWFuture<Exception>(this);

    }

    //------------------------------------------ HWFuture --------------------------------------------------------------

    public IHWFuture<Exception> getConnResult() { return fConnResult; }

    @Override
    public int getErrorCode(IHWFuture future) {
        return connErrCode;
    }

    @Override
    public Exception getPayload(IHWFuture future) {
        return connResult;
    }

    //------------------------------------------ Main Loop -------------------------------------------------------------

    public void run() {
        try {
            InetSocketAddress sAddr = new InetSocketAddress(serverAddress, port);

            socket = new Socket();
            socket.setSoTimeout(2000);
            socket.connect(sAddr);
            socket.setSoTimeout(200);

            reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));

            connErrCode = IHWFuture.ERRORCodes.OK;

        } catch (IOException e) {

            connErrCode = IHWFuture.ERRORCodes.UNKNOWN;
            crashed = true;
            crashRsn = e;
            connResult = e;

        }

        GreetListener gl = new GreetListener(lMgr);
        gl.reportMgr(this);
        requestsLockedBy = gl;

        fConnResult.pokeListeners();

        while (!killed && !crashed) {

            if (!isActive())
                return;

            try {
                if (!requestsLocked()) {
                    sendPendingRequests();
                }
                String ln = reader.readLine();

                if (ln == null || ln.isEmpty()) {
                    lMgr.reportClosed(false);
                }
                JSONObject msg = new JSONObject(ln);

                boolean handled = false;

                for (int i = (listeners.size() -1); i >= 0; i--) {
                    IMessageListener l = listeners.get(i);
                    if (l.onMessage(msg))
                        handled = true;
                }
                if (!handled)
                    lMgr.cdk_sendLog(this, FINE, "A message has not been handled by any Listener!");

            } catch (IOException e) {
                if (!(e instanceof SocketTimeoutException)) {
                    lMgr.cdk_sendLog(this, SEVERE, "IOException on reading socket");
                    lMgr.cdk_sendLog(this, SEVERE, e);
                    lMgr.reportClosed(true);
                }
            } catch (JSONException e) {
                lMgr.cdk_sendLog(this, WARNING, "JSONException on parsing message");
                lMgr.cdk_sendLog(this, WARNING, e);
            }
        }
    }

    //--------------------------------------------- Requests -----------------------------------------------------------

    @API(APILevel = 3)
    public synchronized void queueRequest(IRequest request) {
        int i = genCID();
        if (i == 0)
            request.reportFail(new OutOfCIDsException());
        cIDs.put(request, i);
        request.reportCommID(i);
        requestQueue.add(request);
    }

    @API(APILevel = 3)
    public synchronized boolean unlockQueue(IRequest key) {
        if (requestsLockedBy == key) {
            requestsLockedBy = null;
            sendPendingRequests();
            return true;
        }
        return false;
    }

    @API(APILevel = 3)
    public boolean isQueued(IRequest request){
        return requestQueue.contains(request);
    }

    @API(APILevel = 3)
    public void registerListener(IMessageListener l) {
        if (!listeners.contains(l))
            listeners.add(l);
    }

    @API(APILevel = 3)
    public synchronized void unregisterListener(IMessageListener l) {
        listeners.remove(l);
    }

    @API(APILevel = 3)
    public synchronized void unregisterRequest(IRequest r) {
        requestQueue.remove(r);
        cIDs.remove(r);
        if (requestsLockedBy == r) unlockQueue(r);
    }

    //--------------------------------------------- Misc. --------------------------------------------------------------

    private void repClose(boolean byException) {
        for (int i = (listeners.size() -1); i >= 0; i--)
            listeners.get(i).onClosed(byException);
    }

    public synchronized boolean isCrashed() {
        return crashed;
    }

    public synchronized boolean isActive() {
        return isCrashed() || socket.isConnected();
    }

    public synchronized void kill() {
        try {
            if (socket.isConnected()) {
                socket.close();
            }
        } catch (IOException e) {
            lMgr.sendLog(this, SEVERE, "An error occurred while trying to close the connection: IOException");
            lMgr.sendLog(this, SEVERE, e);
            //Just abandon socket
        }
        killed = true;
        lMgr.reportClosed(false);
    }

    //--------------------------------------------- Internals ----------------------------------------------------------

    private void sendPendingRequests() {
        if (!requestQueue.isEmpty()) {
            ArrayList<IRequest> sent = new ArrayList<IRequest>();
            for (IRequest r : requestQueue) {
                if (r.locksQueue()) {
                    requestsLockedBy = r;
                }
                sendRequest(r);
                sent.add(r);
                if (requestsLocked())
                    break;
            }
            for (IRequest r : sent) {
                requestQueue.remove(r);
            }
        }
    }

    private void sendRequest(IRequest r) {
        try {
            JSONObject msg = r.getRequestMsg();
            msg.put("commID", cIDs.get(r));

            String sMsg = msg.toString(0);
            sMsg = sMsg.replaceAll("\n", "") + "\n";
            writer.write(sMsg);
            writer.flush();
            r.poke();
        } catch (IOException e) {
            r.reportFail(e);
            lMgr.sendLog(this, SEVERE, "Error sending request " + r.getUniqueID());
            lMgr.sendLog(this, SEVERE, e);
            lMgr.reportClosed(true);
        }
    }

    private boolean requestsLocked() {
        return requestsLockedBy != null;
    }

    private int genCID() {
        int i;
        int x = 0;
        do {
            i = rnd.nextInt(900) + 100;
        } while (cIDs.containsValue(i) && ++x < 1000);

        if (x >= 1000)
            return 0;
        return i;
    }
}
