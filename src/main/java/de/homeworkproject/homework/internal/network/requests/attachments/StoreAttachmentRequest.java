package de.homeworkproject.homework.internal.network.requests.attachments;


import de.homeworkproject.homework.api.error.Error;
import de.homeworkproject.homework.api.event.network.CloseReason;
import de.homeworkproject.homework.api.filetransfer.IFTToken;
import de.homeworkproject.homework.api.future.IHWFuture;
import de.homeworkproject.homework.api.homework.IHomeworkAttachment;
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
 * Created by Life4YourGames on 15.11.16.
 */
public class StoreAttachmentRequest implements IHWConnListener {

    private JSONObject REQ = new JSONObject("{\n\"command\": \"postasset\"\n}");

    private String id;
    private int cid;
    private HWFuture<IFTToken> future;
    private CDKConnectionBase conn;
    private LogManager lMgr;

    //------------------------------------------------------------------------------------------------------------------

    public StoreAttachmentRequest(LogManager logger, CDKConnectionBase conn) {
        lMgr = logger;
        this.conn = conn;
        this.future = new HWFuture<IFTToken>();
    }


    private void genID() {
        id = this.toString();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        id = id + sdf.format(cal.getTime());
    }

    //------------------------------------- Payload/Result -------------------------------------------------------------

    @API(APILevel = 2)
    public void setLocation(IHomeworkAttachment obj) {
        REQ.put("location", obj.getJSON());
    }

    public IHWFuture<IFTToken> getFuture() {
        return future;
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

        if (!msg.optString("handler", "null").equals("de.mlessmann.commands.getasset"))
            return false;

        int status = msg.optInt("status", 0);
        //OK - No upload required
        if (status == 200) {
            future.setError(Error.OK);
            future.pokeListeners();
            conn.unregisterListener(this);
            return true;
        }
        if (status == 201) {
            future.setError(Error.OK);
            future.setPayload(new FTToken(msg.getString("token"), IFTToken.Direction.GET, msg.getInt("port")));
            future.pokeListeners();
            conn.unregisterListener(this);
            return true;
        }
        if (status == 204) {
            //NO CONTENT (Asset not on server)
            future.setError(Error.of(Error.ErrorCode.NOTFOUND));
            future.pokeListeners();
            conn.unregisterListener(this);
            return true;
        }

        if (status == 503) {
            //UNAVAILABLE
            future.setError(Error.of(Error.ErrorCode.UNAVAILABLE));
            future.pokeListeners();
            conn.unregisterListener(this);
            return true;
        }
        if (status == 403) {
            //Forbidden
            future.setError(Error.of(Error.ErrorCode.FORBIDDEN));
            future.pokeListeners();
            conn.unregisterListener(this);
        }
        if (status == 423) {
            //LOCKED (Server didn't authorize the transfer)
            future.setError(Error.of(Error.ErrorCode.LOCKED));
            future.pokeListeners();
            conn.unregisterListener(this);
            return true;
        }
        if (status == 404) {
            future.setError(Error.of(Error.ErrorCode.NOTFOUND));
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
