package de.mlessmann.internals.networking.requests;

import de.mlessmann.api.annotations.API;
import de.mlessmann.api.networking.IMessageListener;
import de.mlessmann.api.networking.IRequest;
import de.mlessmann.exceptions.OutOfCIDsException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Created by Life4YourGames on 08.08.16.
 */
@API(APILevel = 3)
public class RequestMgr implements Runnable {

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private IRequest requestsLockedBy = null;

    //TODO: Add an integration to the HWMgrs error handling system
    private boolean crashed;
    private Exception crashRsn;
    private boolean killed = false;

    private List<IMessageListener> listeners = new ArrayList<IMessageListener>();
    private List<IRequest> requestQueue = new ArrayList<IRequest>();
    private Map<IRequest, Integer> cIDs = new HashMap<IRequest, Integer>();
    private Random rnd = new Random();

    public RequestMgr(Socket socket) {

        this.socket = socket;

    }

    //------------------------------------------ Main Loop -------------------------------------------------------------

    public void run() {

        while (!killed && !crashed) {

            if (!crashed && reader == null && writer == null) {

                try {

                    reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                    writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));

                } catch (IOException e) {

                    crashed = true;
                    crashRsn = e;

                }

            }

            if (!isActive())
                return;

            try {

                String ln = reader.readLine();

                JSONObject msg = new JSONObject(ln);

                for (IMessageListener l : listeners) {

                    l.onMessage(msg);

                }

                if (!requestsLocked()) {
                    sendPendingRequests();
                }

            } catch (IOException e) {

                crashed = true;
                crashRsn = e;

            } catch (JSONException e) {
                //TODO: Replace this with an integration to the HWMgr error handling system
                return;
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

        if (requestsLocked())
            requestQueue.add(request);
        else {
            sendRequest(request);

            if (request.locksQueue())
                requestsLockedBy = request;
        }

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

    //--------------------------------------------- Misc. --------------------------------------------------------------


    public boolean isCrashed() {

        return crashed;

    }

    public boolean isActive() {

        return isCrashed() || socket.isConnected();

    }

    public void kill() {
        killed = true;
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

            writer.write(msg.toString(0) + "\n");
            r.poke();

        } catch (IOException e) {

            r.reportFail(e);
            //TODO: Add to error integration

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
