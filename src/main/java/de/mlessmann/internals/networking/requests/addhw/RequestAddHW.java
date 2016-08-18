package de.mlessmann.internals.networking.requests.addhw;

import de.mlessmann.api.annotations.API;
import de.mlessmann.api.data.IHWCarrier;
import de.mlessmann.api.data.IHWFuture;
import de.mlessmann.api.data.IHWFutureProvider;
import de.mlessmann.api.networking.Errors;
import de.mlessmann.api.networking.IMessageListener;
import de.mlessmann.api.networking.IRequest;
import de.mlessmann.internals.data.HWFuture;
import de.mlessmann.internals.networking.requests.RequestMgr;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Life4YourGames on 11.08.16.
 */
public class RequestAddHW implements IRequest, IMessageListener, IHWFutureProvider<Boolean> {

    private JSONObject REQ = new JSONObject("{\n\"command\": \"addhw\"\n}");

    private String id;
    private int cid;
    private int errorCode = 0;
    private Boolean result = null;
    private HWFuture<Boolean> future;
    private RequestMgr reqMgr;

    //------------------------------------------------------------------------------------------------------------------

    public RequestAddHW() {

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
    public void setHW(IHWCarrier obj) {

        REQ.put("homework", obj.getJSON());

    }

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

        result = false;

    }

    @Override
    public IMessageListener getListener() {
        return this;
    }

    //------------------------------------ IMessageListener ------------------------------------------------------------


    @Override
    public void onMessage(JSONObject msg) {
        if (msg.optInt("commID", -1) != cid) return;

        if (!msg.optString("handler", "null").equals("de.mlessmann.commands.addHW"))
            return;

        int status = msg.optInt("status", 0);
        if (status == 201) {
            errorCode = HWFuture.ERRORCodes.OK;
            future.pokeListeners();
            reqMgr.unregisterListener(this);
            reqMgr.unregisterRequest(this);
            return;
        }

        if (msg.optString("payload_type", "null").equals("error")) {
            JSONObject e = msg.getJSONObject("payload");

            String err = e.optString("error", "null");

            if (err.equals(Errors.ProtoError))
                errorCode = HWFuture.ERRORCodes.PROTOError;
            else if (err.equals(Errors.LOGINREQError))
                errorCode = HWFuture.ERRORCodes.LOGINREQ;
            else if (err.equals(Errors.InsuffPermError))
                errorCode = HWFuture.ERRORCodes.INSUFFPERM;
            else if (err.equals(Errors.AddHWError)) {
                if (status == 400)
                    errorCode = HWFuture.ERRORCodes.INVALIDPAYLOAD;
                else
                    errorCode = HWFuture.ERRORCodes.UNKNOWN;
            } else {
                errorCode = HWFuture.ERRORCodes.UNKNOWN;
            }

            future.pokeListeners();
            reqMgr.unregisterListener(this);
            reqMgr.unregisterRequest(this);

        }

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

}
