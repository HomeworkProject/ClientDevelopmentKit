package de.mlessmann.internals.networking.requests.list;

import de.mlessmann.api.data.IHWFuture;
import de.mlessmann.api.data.IHWFutureProvider;
import de.mlessmann.api.data.IHWGroupMapping;
import de.mlessmann.api.networking.Errors;
import de.mlessmann.api.networking.IMessageListener;
import de.mlessmann.api.networking.IRequest;
import de.mlessmann.common.annotations.API;
import de.mlessmann.internals.data.HWFuture;
import de.mlessmann.internals.data.HWGroupMapping;
import de.mlessmann.internals.logging.LMgr;
import de.mlessmann.internals.networking.requests.RequestMgr;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Life4YourGames on 09.08.16.
 */
public class RequestList implements IRequest, IHWFutureProvider<IHWGroupMapping>, IMessageListener {

    private final JSONObject REQ = new JSONObject("{\n\"command\": \"list\"\n}");

    private String id;
    private int cid;
    private int errorCode = 0;
    private IHWGroupMapping result = null;
    private HWFuture<IHWGroupMapping> future;
    private RequestMgr reqMgr;
    private LMgr lMgr;

    //------------------------------------------------------------------------------------------------------------------

    public RequestList(LMgr logger) {
        lMgr = logger;
        genID();
        this.future = new HWFuture<IHWGroupMapping>(this);
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

    @API(APILevel = 2)
    public IHWFuture<IHWGroupMapping> getFuture() {
        return this.future;
    }

    //------------------------------------- IRequest -------------------------------------------------------------------

    @Override
    public String getUniqueID() {
        return id;
    }

    @Override
    public boolean locksQueue() {
        return false;
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
        result = null;
        errorCode = IHWFuture.ERRORCodes.UNKNOWN;
        future.pokeListeners();
    }

    @Override
    public IMessageListener getListener() {
        return this;
    }

    //------------------------------------ IMessageListener ------------------------------------------------------------


    @Override
    public boolean onMessage(JSONObject msg) {
        if (msg.optInt("commID", -1) != cid) return false;

        if (msg.optString("handler", "null").equals("de.mlessmann.commands.list")) {
            if (msg.getInt("status") == 200) {

                HWGroupMapping mapping = null;
                JSONObject o = msg.optJSONObject("payload");
                if (o!=null) {
                    mapping = new HWGroupMapping(o);
                }
                result = mapping;
                errorCode = mapping!=null ? IHWFuture.ERRORCodes.OK : IHWFuture.ERRORCodes.UNKNOWN;
                reqMgr.unregisterListener(this);
                reqMgr.unregisterRequest(this);
                future.pokeListeners();
                return true;

            } else if (msg.optString("payload_type", "null").equals("error")) {
                JSONObject e = msg.getJSONObject("payload");
                if (e.optString("error", "null").equals(Errors.NotFoundError)) {
                    result = null;
                    errorCode = IHWFuture.ERRORCodes.NOTFOUNDERR;
                } else {
                    result = null;
                    errorCode = IHWFuture.ERRORCodes.UNKNOWN;
                }
                reqMgr.unregisterListener(this);
                reqMgr.unregisterRequest(this);
                future.pokeListeners();
                return true;
            }
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
    public IHWGroupMapping getPayload(IHWFuture future) {
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
