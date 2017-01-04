package de.mlessmann.homework.internal;

import de.mlessmann.homework.api.CDK;
import de.mlessmann.homework.api.event.ICDKConnectionEvent;
import de.mlessmann.homework.api.event.ICDKEvent;
import de.mlessmann.homework.api.event.network.CloseReason;
import de.mlessmann.homework.api.event.network.ConnectionStatus;
import de.mlessmann.homework.api.event.network.InterruptReason;
import de.mlessmann.homework.api.provider.IHWProvider;
import de.mlessmann.homework.api.provider.IHWProviderConnInfo;
import de.mlessmann.homework.internal.error.CDKCertificateCloseException;
import de.mlessmann.homework.internal.event.*;
import de.mlessmann.homework.internal.network.CDKX509TrustManager;
import de.mlessmann.homework.internal.network.IHWConnListener;
import de.mlessmann.homework.internal.network.requests.greeting.GreetListener;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Life4YourGames on 15.12.16.
 */
public class CDKConnectionBase extends Thread {

    private CDK cdk;
    private CDKX509TrustManager trustManager;

    private IHWProvider provider;
    private String host = null;
    private int port = 0;
    private int sslPort = 0;

    private Socket sock;
    private SocketAddress socketAddr;
    private BufferedReader reader;
    private BufferedWriter writer;
    private boolean terminated;

    private List<IHWConnListener> listeners;
    private List<Integer> cIDs;

    public CDKConnectionBase(CDK cdk) {
        this.cdk = cdk;
        trustManager = new CDKX509TrustManager(this);
        listeners = new ArrayList<>();
        cIDs = new ArrayList<Integer>();
    }

    public void setProvider(IHWProvider provider) {
        this.provider = provider;
        IHWProviderConnInfo ci = provider.getConnInfo();
        setProvider(ci.getHost(), ci.getPort(), ci.getSSLPort());
    }

    public void setProvider(String host, int port, int sslPort) {
        this.host = host;
        this.port = port;
        this.sslPort = sslPort;
    }

    @Override
    public void run() {
        this.fireEvent(new CDKConnEvent(this, ConnectionStatus.CONNECTING));
        ICDKConnectionEvent.Interrupted event = null;
        boolean sslFailed = true;
        if (sslPort != 0) {
            try {
                socketAddr = new InetSocketAddress(host, sslPort);

                SSLContext ctx = trustManager.createSSLContext();
                sock = ctx.getSocketFactory().createSocket();
                sock.connect(socketAddr, 4000);
                sock.setKeepAlive(true);
                sslFailed = false;
            } catch (Exception ex) {
                if (ex instanceof CDKCertificateCloseException) {
                    this.fireEvent(new CDKConnCloseEvent(this, CloseReason.REJECTED_X509));
                    return;
                } else {
                    event = new CDKConnInterruptExcEvent(this, InterruptReason.SSL_UNAVAILABLE, ex);
                    this.fireEvent(event);
                }
            }
        } else {
            event = new CDKConnInterruptEvent(this, InterruptReason.SSL_UNAVAILABLE);
            this.fireEvent(event);
        }
        if (event!=null && event.isCancelled()) {
            this.fireEvent(new CDKConnCloseEvent(this, CloseReason.CONNECT_FAILED));
        }
        //Try plaintext
        try {
            socketAddr = new InetSocketAddress(host, port);
            sock = new Socket();
            sock.connect(socketAddr, 4000);
        } catch (IOException e) {
            this.fireEvent(new CDKConnCloseEvent(this, CloseReason.CONNECT_FAILED));
            return;
        }
        //Moved to greeting listener
        //this.fireEvent(new CDKConnEvent(this, ConnectionStatus.CONNECTED));
        this.registerListener(new GreetListener(cdk.getLogManager(), this));

        main();
    }

    private void main() {

        try {
            sock.setSoTimeout(500);
            reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        } catch (IOException e) {
            this.fireEvent(new CDKConnCloseExcEvent(this, CloseReason.EXCEPTION, e));
            return;
        }
        terminated = false;
        int timeOutCount = 0;
        while (!terminated) {
            try {
                if (timeOutCount>6) {
                    sendMessage("ping\n");
                }
                String line = reader.readLine();
                if (line == null) {
                    this.fireEvent(new CDKConnCloseEvent(this, CloseReason.LOST));
                    terminated = true;
                } else if ("pong".equals(line)) {
                    timeOutCount = 0;
                } else {
                    JSONObject o = new JSONObject(line);
                    processJSON(o);
                }
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException) {
                    timeOutCount++;
                }
            } catch (JSONException e) {
                this.fireEvent(new CDKExceptionEvent(this, e));
            }

        }
    }

    private void processJSON(JSONObject obj) {
        int cID = obj.optInt("commID", 0);
        Integer k = (cID*-1);
        if (cIDs.indexOf(k) > -1) {
            cIDs.remove(k);
        }
        for (int i = listeners.size()-1; i >= 0; i--)
            listeners.get(i).processJSON(obj);
    }

    private synchronized int genCID() {
        Random rnd = new Random();
        Integer i = 0;
        do {
            i = rnd.nextInt();
        } while (cIDs.indexOf(i) > -1);
        return i;
    }

    public int sendJSON(JSONObject obj) {
        int cID = obj.optInt("commID", 0);
        if (cID == 0) {
            cID = genCID();
            obj.put("commID", cID);
        }
        Integer k = (cID*-1);
        if (!(cIDs.indexOf(k) > -1))
            cIDs.add(k);
        sendMessage(obj.toString(0).replaceAll("\n", "") + "\n");
        return cID;
    }

    private void sendMessage(String msg) {
        try {
            writer.write(msg);
            writer.flush();
        } catch (IOException e) {
            this.fireEvent(new CDKExceptionEvent(this, e));
        }
    }

    public void registerListener(IHWConnListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(IHWConnListener listener) {
        listeners.remove(listener);
    }

    //----

    public void kill() {
        terminated = true;
        this.fireEvent(new CDKConnCloseEvent(this, CloseReason.KILLED));
    }

    public void fireEvent(ICDKEvent event) {
        cdk.fireEvent(event);
    }

    //Getter
    protected CDK getCDK() {
        return cdk;
    }

    public CDKX509TrustManager getTrustManager() {
        return trustManager;
    }

    public String getHost() { return host; }
}
