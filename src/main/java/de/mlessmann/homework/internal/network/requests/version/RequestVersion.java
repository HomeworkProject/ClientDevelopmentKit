package de.mlessmann.homework.internal.network.requests.version;

import de.mlessmann.common.Common;
import de.mlessmann.common.annotations.API;
import de.mlessmann.homework.api.CDK;
import de.mlessmann.homework.api.error.Error;
import de.mlessmann.homework.api.event.network.CloseReason;
import de.mlessmann.homework.api.future.IHWFuture;
import de.mlessmann.homework.internal.CDKConnectionBase;
import de.mlessmann.homework.internal.future.HWFuture;
import de.mlessmann.homework.internal.logging.LogManager;
import de.mlessmann.homework.internal.network.IHWConnListener;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Life4YourGames on 08.08.16.
 * Class implements the complete Version check.
 * Also an example that Request, Listener and Provider can be the same object
 */
public class RequestVersion implements IHWConnListener {

    static final JSONObject REQ = new JSONObject("{\n\"command\": \"getinfo\"\n}");

    private String id;
    private int cid;
    private HWFuture<Boolean> future;
    private CDKConnectionBase conn;
    private LogManager lMgr;

    //------------------------------------------------------------------------------------------------------------------

    public RequestVersion(LogManager logger, CDKConnectionBase conn) {
        lMgr = logger;
        this.conn = conn;
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
        if (msg.optString("handler", "null").equals("de.mlessmann.commands.getProtocolVersion")) {
            String version = msg.getString("protoVersion");
            String currentVersion = CDK.PROTOVERSION;

            future.setPayload(Common.areCompatible(version, currentVersion));
            conn.unregisterListener(this);
            //reqMgr.unlockQueue(this);
            future.pokeListeners();
            return true;
        }
        return false;
    }
}

