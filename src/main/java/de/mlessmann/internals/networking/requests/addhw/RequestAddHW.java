package de.mlessmann.internals.networking.requests.addhw;

import de.mlessmann.api.annotations.API;
import de.mlessmann.api.data.IHWCarrier;
import de.mlessmann.api.data.IHWFuture;
import de.mlessmann.api.data.IHWFutureProvider;
import de.mlessmann.api.data.IHWObj;
import de.mlessmann.api.networking.Errors;
import de.mlessmann.api.networking.IMessageListener;
import de.mlessmann.api.networking.IRequest;
import de.mlessmann.internals.data.HWFuture;
import de.mlessmann.internals.data.HWObject;
import de.mlessmann.internals.networking.requests.RequestMgr;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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



    }

    @Override
    public void reportMgr(RequestMgr mgr) {
        reqMgr = mgr;
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
