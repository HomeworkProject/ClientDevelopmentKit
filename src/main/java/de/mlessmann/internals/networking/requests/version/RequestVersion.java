package de.mlessmann.internals.networking.requests.version;

import de.mlessmann.api.data.IHWFuture;
import de.mlessmann.api.data.IHWFutureProvider;
import de.mlessmann.api.networking.IMessageListener;
import de.mlessmann.api.networking.IRequest;
import de.mlessmann.common.Common;
import de.mlessmann.common.annotations.API;
import de.mlessmann.internals.data.HWFuture;
import de.mlessmann.internals.logging.LMgr;
import de.mlessmann.internals.networking.requests.RequestMgr;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Life4YourGames on 08.08.16.
 * Class implements the complete Version check.
 * Also an example that Request, Listener and Provider can be the same object
 */
public class RequestVersion implements IRequest, IMessageListener, IHWFutureProvider<Boolean>{

    static final JSONObject REQ = new JSONObject("{\n\"command\": \"getinfo\"\n}");

    private String id;
    private int cid;
    private int errorCode = 0;
    private Boolean isCompatible = null;
    private HWFuture<Boolean> future;
    private RequestMgr reqMgr;
    private LMgr lMgr;

    //------------------------------------------------------------------------------------------------------------------

    public RequestVersion(LMgr logger) {

        lMgr = logger;

        genID();

        this.future = new HWFuture<Boolean>(this);

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

    //------------------------------------- IRequest -------------------------------------------------------------------

    @Override
    public String getUniqueID() {
        return id;
    }

    @Override
    public boolean locksQueue() {
        return true;
    }

    @Override
    public JSONObject getRequestMsg() {
        return REQ;
    }

    @Override
    public void reportCommID(int cid) {
        this.cid = cid;
    }

    @Override
    public void poke() {
        //Don't care
    }

    @Override
    public void reportFail(Exception e) {
        //This means, not compatible!
        isCompatible = Boolean.FALSE;
    }

    @Override
    public IMessageListener getListener() {
        return this;
    }

    //------------------------------------ IMessageListener ------------------------------------------------------------


    @Override
    public boolean onMessage(JSONObject msg) {
        if (msg.optString("handler", "null").equals("de.mlessmann.commands.getProtocolVersion")) {

            String version = msg.getString("protoVersion");
            String currentVersion = API.PROTOVERSION;

            isCompatible = Common.areCompatible(version, currentVersion);

            reqMgr.unregisterListener(this);
            reqMgr.unregisterRequest(this);
            //reqMgr.unlockQueue(this);
            future.pokeListeners();
            return true;
        }
        return false;
    }

    @Override
    public void reportMgr(RequestMgr mgr) {
        if (reqMgr != null)
            reqMgr.unregisterListener(this);
        reqMgr = mgr;
        reqMgr.registerListener(this);
    }

    //------------------------------------ IHWFutureProvider -----------------------------------------------------------


    @Override
    public Boolean getPayload(IHWFuture future) {
        if (future == this.future)
            return isCompatible;
        else
            return null;
    }

    public int getErrorCode(IHWFuture future) {
        if (future == this.future)
            return errorCode;
        else
            return 0;
    }
}

