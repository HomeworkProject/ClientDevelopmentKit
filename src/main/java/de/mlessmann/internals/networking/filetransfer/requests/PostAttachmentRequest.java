package de.mlessmann.internals.networking.filetransfer.requests;

import de.mlessmann.api.data.IFTToken;
import de.mlessmann.api.data.IHWAttachmentLocation;
import de.mlessmann.api.data.IHWFuture;
import de.mlessmann.api.data.IHWFutureProvider;
import de.mlessmann.api.networking.CloseReason;
import de.mlessmann.api.networking.Errors;
import de.mlessmann.api.networking.IMessageListener;
import de.mlessmann.api.networking.IRequest;
import de.mlessmann.common.annotations.API;
import de.mlessmann.common.parallel.IFuture;
import de.mlessmann.internals.data.FTToken;
import de.mlessmann.internals.data.HWFuture;
import de.mlessmann.internals.logging.LMgr;
import de.mlessmann.internals.networking.requests.RequestMgr;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Life4YourGames on 15.11.16.
 */
public class PostAttachmentRequest implements IRequest, IMessageListener, IHWFutureProvider<IFTToken> {

    private JSONObject REQ = new JSONObject("{\n\"command\": \"postasset\"\n}");

    private String id;
    private int cid;
    private int errorCode = 0;
    private Object error = null;
    private IFTToken result = null;
    private HWFuture<IFTToken> future;
    private RequestMgr reqMgr;
    private LMgr lMgr;

    //------------------------------------------------------------------------------------------------------------------

    public PostAttachmentRequest(LMgr logger) {
        lMgr = logger;
        genID();
        this.future = new HWFuture<IFTToken>(this);
    }


    private void genID() {

        id = this.toString();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        id = id + sdf.format(cal.getTime());
    }

    //------------------------------------- Payload/Result -------------------------------------------------------------

    @API(APILevel = 2)
    public void setLocation(IHWAttachmentLocation obj) {
        REQ.put("location", obj.getJSON());
    }

    @API(APILevel = 2)
    public IHWFuture<IFTToken> getFuture() {
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
        result = new FTToken(null, IFTToken.Direction.UNKNOWN);
        errorCode = IHWFuture.ERRORCodes.UNKNOWN;
        future.pokeListeners();
    }

    @Override
    public IMessageListener getListener() {
        return this;
    }

    //------------------------------------ IMessageListener ------------------------------------------------------------

    @Override
    public void onClosed(CloseReason rsn) {
        result = null;
        error = rsn;
        errorCode = IHWFuture.ERRORCodes.CLOSED;
        future.pokeListeners();
    }

    @Override
    public boolean onMessage(JSONObject msg) {
        if (msg.optInt("commID", -1) != cid) return false;

        if (!msg.optString("handler", "null").equals("de.mlessmann.commands.getasset"))
            return false;

        int status = msg.optInt("status", 0);
        if (status == 201) {
            errorCode = HWFuture.ERRORCodes.OK;
            result = new FTToken(msg.getString("token"), IFTToken.Direction.GET);
            future.pokeListeners();
            reqMgr.unregisterListener(this);
            reqMgr.unregisterRequest(this);
            return true;
        }
        if (status == 204) {
            //NO CONTENT (Asset not on server)
            errorCode = HWFuture.ERRORCodes.NOCONTENT;
            future.pokeListeners();
            reqMgr.unregisterListener(this);
            reqMgr.unregisterRequest(this);
            return true;
        }

        if (status == 503) {
            //UNAVAILABLE
            errorCode = HWFuture.ERRORCodes.UNAVAILABLE;
            future.pokeListeners();
            reqMgr.unregisterListener(this);
            reqMgr.unregisterRequest(this);
            return true;
        }
        if (status == 403) {
            //Forbidden
            errorCode = HWFuture.ERRORCodes.INSUFFPERM;
            future.pokeListeners();
            reqMgr.unregisterListener(this);
            reqMgr.unregisterRequest(this);
        }
        if (status == 423) {
            //LOCKED (Server didn't authorize the transfer)
            errorCode = HWFuture.ERRORCodes.LOCKED;
            future.pokeListeners();
            reqMgr.unregisterListener(this);
            reqMgr.unregisterRequest(this);
            return true;
        }
        if (status == 404) {
            errorCode = HWFuture.ERRORCodes.NOTFOUNDERR;
            future.pokeListeners();
            reqMgr.unregisterListener(this);
            reqMgr.unregisterRequest(this);
            return true;
        }

        if (msg.optString("payload_type", "null").equals("error")) {
            JSONObject e = msg.getJSONObject("payload");

            String err = e.optString("error", "null");

            if (err.equals(Errors.ProtoError))
                errorCode = HWFuture.ERRORCodes.PROTOError;
            else if (err.equals(Errors.LOGINREQError))
                errorCode = HWFuture.ERRORCodes.LOGINREQ;
            else {
                errorCode = HWFuture.ERRORCodes.UNKNOWN;
            }

            reqMgr.unregisterListener(this);
            reqMgr.unregisterRequest(this);
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
    public IFTToken getPayload(IFuture future) {
        if (future == this.future)
            return result;
        else
            return null;
    }

    @Override
    public int getErrorCode(IHWFuture future) {
        if (future == this.future)
            return errorCode;
        else
            return 0;
    }

    @Override
    public Object getError(IHWFuture future) {
        if (future == this.future)
            return error;
        else
            return null;
    }
}
