package de.mlessmann.homework.internal.network.requests.delhw;


import de.mlessmann.common.annotations.API;
import de.mlessmann.homework.api.error.Error;
import de.mlessmann.homework.api.event.network.CloseReason;
import de.mlessmann.homework.api.future.IHWFuture;
import de.mlessmann.homework.internal.CDKConnectionBase;
import de.mlessmann.homework.internal.future.HWFuture;
import de.mlessmann.homework.internal.logging.LogManager;
import de.mlessmann.homework.internal.network.IHWConnListener;
import de.mlessmann.homework.internal.network.requests.Errors;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Life4YourGames on 15.08.16.
 */
public class RequestDelHW implements IHWConnListener {

    private JSONObject REQ = new JSONObject("{\n\"command\": \"delhw\"\n}");

    private String id;
    private int cid;
    private HWFuture<Boolean> future;
    private CDKConnectionBase conn;
    private LogManager lMgr;

    //------------------------------------------------------------------------------------------------------------------

    public RequestDelHW(LogManager logger, CDKConnectionBase conn) {
        lMgr = logger;
        this.conn = conn;
        genID();
        this.future = new HWFuture<Boolean>();
    }


    private void genID() {

        id = this.toString();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        id = id + sdf.format(cal.getTime());

    }

    //------------------------------------- Payload/Result -------------------------------------------------------------

    @API(APILevel = 2)
    public void setID(String id) {
        REQ.put("id", id);
    }

    @API(APILevel = 2)
    public void setDate(int yyyy, int MM, int dd) {
        JSONArray a = new JSONArray();
        a.put(yyyy);
        a.put(MM);
        a.put(dd);

        REQ.put("date", a);
    }

    public IHWFuture<Boolean> getFuture() {
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

    @Override
    public boolean processJSON(JSONObject msg) {
        if (msg.optInt("commID", -1) != cid) return false;

        if (!msg.optString("handler", "null").equals("de.mlessmann.commands.delhw"))
            return false;

        int status = msg.optInt("status", 0);
        if (status == 200) {
            future.setError(Error.OK);
            future.pokeListeners();
            conn.unregisterListener(this);
            return true;
        }

        if (msg.optString("payload_type", "null").equals("error")) {
            JSONObject e = msg.getJSONObject("payload");

            String err = e.optString("error", "null");

            if (err.equals(Errors.ProtoError))
                future.setError(Error.of(Error.ErrorCode.BADREQUEST));
            else if (err.equals(Errors.LOGINREQError))
                future.setError(Error.of(Error.ErrorCode.UNAUTHORIZED));
            else if (err.equals(Errors.InsuffPermError))
                future.setError(Error.of(Error.ErrorCode.FORBIDDEN));
            else if (err.equals(Errors.DelHWError))
                future.setError(Error.of(Error.ErrorCode.UNKNOWN));
            else if (err.equals(Errors.DATETIMEError))
                future.setError(Error.of(Error.ErrorCode.DATEError));
            else {
                future.setError(Error.of(Error.ErrorCode.UNKNOWN));
            }

            conn.unregisterListener(this);
            future.pokeListeners();
            return true;
        }
        return false;
    }
}
