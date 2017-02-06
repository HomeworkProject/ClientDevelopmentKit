package de.homeworkproject.homework.internal.network.requests.greeting;


import de.homeworkproject.homework.api.event.ICDKConnectionEvent;
import de.homeworkproject.homework.api.event.network.CloseReason;
import de.homeworkproject.homework.api.event.network.ConnectionStatus;
import de.homeworkproject.homework.api.event.network.InterruptReason;
import de.homeworkproject.homework.internal.CDKConnectionBase;
import de.homeworkproject.homework.internal.event.CDKConnEvent;
import de.homeworkproject.homework.internal.event.CDKConnInterruptEvent;
import de.homeworkproject.homework.internal.logging.LogManager;
import de.homeworkproject.homework.internal.network.IHWConnListener;
import de.homeworkproject.homework.internal.network.requests.version.RequestVersion;
import de.mlessmann.common.parallel.IFuture;
import de.mlessmann.common.parallel.IFutureListener;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Life4YourGames on 18.08.16.
 */
public class GreetListener implements IHWConnListener {

    static final JSONObject REQ = new JSONObject();

    private String id;
    private int cid;
    private CDKConnectionBase conn;
    private LogManager lMgr;

    //------------------------------------------------------------------------------------------------------------------

    public GreetListener(LogManager logger, CDKConnectionBase conn) {
        lMgr = logger;
        this.conn = conn;
    }


    private void genID() {
        id = this.toString();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        id = id + sdf.format(cal.getTime());
    }

    //------------------------------------ IMessageListener ------------------------------------------------------------

    @Override
    public void onClosed(CloseReason rsn) {
        //Don't care
    }

    @Override
    public boolean processJSON(JSONObject msg) {
        if (msg.optInt("commID", -1) == 1) {
            if (msg.optInt("status", -1) == 200) {
                if (!conn.checksVersion()) {
                    conn.fireEvent(new CDKConnEvent(this, ConnectionStatus.CONNECTED));
                } else {
                    final GreetListener l = this;
                    RequestVersion vReq = new RequestVersion(lMgr, conn);
                    vReq.getFuture().registerListener(new IFutureListener() {
                        @Override
                        public void onFutureAvailable(IFuture<?> future) {
                            if ((Boolean) future.get()) {
                                conn.fireEvent(new CDKConnEvent(l, ConnectionStatus.CONNECTED));
                            } else {
                                ICDKConnectionEvent.Interrupted event = new CDKConnInterruptEvent(l, InterruptReason.POSSIBLY_INCOMPATIBLE);
                                conn.fireEvent(event);
                                if (event.isCancelled()) {
                                    conn.close();
                                } else {
                                    conn.fireEvent(new CDKConnEvent(l, ConnectionStatus.CONNECTED));
                                }
                            }
                        }
                    });
                    vReq.execute();
                }
                conn.unregisterListener(this);
            }
            return true;
        }
        return false;
    }
}
