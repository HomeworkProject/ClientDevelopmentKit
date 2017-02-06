package de.homeworkproject.homework.internal.network.requests.addhw;

import de.homeworkproject.homework.api.error.Error;
import de.homeworkproject.homework.api.event.network.CloseReason;
import de.homeworkproject.homework.api.future.IHWFuture;
import de.homeworkproject.homework.api.homework.IHWCarrier;
import de.homeworkproject.homework.internal.CDKConnectionBase;
import de.homeworkproject.homework.internal.future.HWFuture;
import de.homeworkproject.homework.internal.logging.LogManager;
import de.homeworkproject.homework.internal.network.IHWConnListener;
import de.homeworkproject.homework.internal.network.requests.Errors;
import de.mlessmann.common.annotations.API;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Life4YourGames on 11.08.16.
 */
public class RequestAddHW implements IHWConnListener {

    private JSONObject REQ = new JSONObject("{\n\"command\": \"addhw\"\n}");

    private String id;
    private int cid;
    private HWFuture<Boolean> future;
    private LogManager lMgr;
    private CDKConnectionBase conn;

    //------------------------------------------------------------------------------------------------------------------

    public RequestAddHW(LogManager logger, CDKConnectionBase conn) {
        lMgr = logger;
        //genID();
        this.future = new HWFuture<Boolean>();
        this.conn = conn;
    }


    private void genID() {
        id = this.toString();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        id = id + sdf.format(cal.getTime());
    }

    //------------------------------------- Payload/Result -------------------------------------------------------------

    @API(APILevel = 2)
    public void setHW(IHWCarrier obj) {
        REQ.put("homework", obj.getJSON());
    }

    @API(APILevel = 2)
    public IHWFuture<Boolean> getFuture() {
        return this.future;
    }

    //------------------------------------- ///////// -------------------------------------------------------------------

    public void execute() {
        conn.registerListener(this);
        cid = conn.queueJSON(REQ);
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

        if (!msg.optString("handler", "null").equals("de.mlessmann.commands.addhw"))
            return false;

        int status = msg.optInt("status", 0);
        if (status == 201) {
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
            else if (err.equals(Errors.AddHWError) && status == 400) {
                future.setError(Error.of(Error.ErrorCode.BADREQUEST));
            } else {
                future.setError(Error.of(Error.ErrorCode.UNKNOWN));
            }

            conn.unregisterListener(this);
            future.pokeListeners();
            return true;
        }
        return false;
    }
}
