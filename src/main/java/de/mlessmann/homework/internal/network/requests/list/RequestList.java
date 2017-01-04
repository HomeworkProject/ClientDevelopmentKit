package de.mlessmann.homework.internal.network.requests.list;


import de.mlessmann.common.annotations.API;
import de.mlessmann.homework.api.error.Error;
import de.mlessmann.homework.api.event.network.CloseReason;
import de.mlessmann.homework.api.future.IHWFuture;
import de.mlessmann.homework.api.session.IHWGroupMapping;
import de.mlessmann.homework.internal.CDKConnectionBase;
import de.mlessmann.homework.internal.future.HWFuture;
import de.mlessmann.homework.internal.homework.HWGroupMapping;
import de.mlessmann.homework.internal.logging.LogManager;
import de.mlessmann.homework.internal.network.IHWConnListener;
import de.mlessmann.homework.internal.network.requests.Errors;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Life4YourGames on 09.08.16.
 */
public class RequestList implements IHWConnListener {

    private final JSONObject REQ = new JSONObject("{\n\"command\": \"list\"\n}");

    private String id;
    private int cid;
    private HWFuture<IHWGroupMapping> future;
    private CDKConnectionBase conn;
    private LogManager lMgr;

    //------------------------------------------------------------------------------------------------------------------

    public RequestList(LogManager logger, CDKConnectionBase conn) {
        lMgr = logger;
        this.conn = conn;
        this.future = new HWFuture<IHWGroupMapping>();
    }


    private void genID() {
        id = this.toString();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        id = id + sdf.format(cal.getTime());
    }

    //------------------------------------- Payload/Result -------------------------------------------------------------

    @API(APILevel = 2)
    public void setGrp(String grp) {
        REQ.put("group", grp);
    }

    public IHWFuture<IHWGroupMapping> getFuture() {
        return future;
    }

    //------------------------------------- ///////// -------------------------------------------------------------------

    public void execute() {
        conn.registerListener(this);
        cid = conn.sendJSON(REQ);
    }

    //------------------------------------ IHWConnListener ------------------------------------------------------------

    @Override
    public void onClosed(CloseReason rsn) {
        future.setError(Error.of(Error.ErrorCode.CLOSED, null));
        future.pokeListeners();
    }

    //------------------------------------ IMessageListener ------------------------------------------------------------


    @Override
    public boolean processJSON(JSONObject msg) {
        if (msg.optInt("commID", -1) != cid) return false;

        if (msg.optString("handler", "null").equals("de.mlessmann.commands.list")) {
            if (msg.getInt("status") == 200) {

                HWGroupMapping mapping = null;
                JSONObject o = msg.optJSONObject("payload");
                if (o!=null) {
                    mapping = new HWGroupMapping(o);
                }
                future.setPayload(mapping);
                future.setError(mapping!=null ? Error.OK : Error.of(Error.ErrorCode.UNKNOWN));
                conn.unregisterListener(this);
                future.pokeListeners();
                return true;

            } else if (msg.optString("payload_type", "null").equals("error")) {
                JSONObject e = msg.getJSONObject("payload");
                if (e.optString("error", "null").equals(Errors.NotFoundError)) {
                    future.setError(Error.of(Error.ErrorCode.NOTFOUND));
                } else {
                    future.setError(Error.of(Error.ErrorCode.UNKNOWN));
                }
                conn.unregisterListener(this);
                future.pokeListeners();
                return true;
            }
        }
        return false;
    }
}
