package de.mlessmann.internals.networking.requests.gethw;

import de.mlessmann.api.annotations.API;
import de.mlessmann.api.data.IHWFuture;
import de.mlessmann.api.data.IHWFutureProvider;
import de.mlessmann.api.data.IHWObj;
import de.mlessmann.api.networking.Errors;
import de.mlessmann.api.networking.IMessageListener;
import de.mlessmann.api.networking.IRequest;
import de.mlessmann.internals.data.HWFuture;
import de.mlessmann.internals.data.HWObject;
import de.mlessmann.internals.logging.LMgr;
import de.mlessmann.internals.networking.requests.RequestMgr;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Life4YourGames on 09.08.16.
 */
public class RequestGetHW implements IRequest, IHWFutureProvider<List<IHWObj>>, IMessageListener {


    static final JSONObject REQ = new JSONObject("{\n\"command\": \"gethw\"\n}");

    private String id;
    private int cid;
    private int errorCode = 0;
    private List<IHWObj> result = null;
    private HWFuture<List<IHWObj>> future;
    private RequestMgr reqMgr;
    private LMgr lMgr;

    //------------------------------------------------------------------------------------------------------------------

    public RequestGetHW(LMgr logger) {

        lMgr = logger;

        genID();

        this.future = new HWFuture<List<IHWObj>>(this);

    }


    private void genID() {

        id = this.toString();

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        id = id + sdf.format(cal.getTime());

    }

    //------------------------------------- Payload/Result -------------------------------------------------------------

    @API(APILevel = 2)
    public void setDate(int yyyy, int MM, int dd) {
        if (REQ.has("dateFrom"))
            REQ.remove("dateFrom");
        if (REQ.has("dateTo"))
            REQ.remove("dateTo");

        JSONArray a = new JSONArray();
        a.put(yyyy);
        a.put(MM);
        a.put(dd);

        REQ.put("date", a);
    }

    @API(APILevel = 2)
    public void setDates(int yyyyFrom, int MMFrom, int ddFrom, int yyyyTo, int MMTo, int ddTo) {
        if (REQ.has("date"))
            REQ.remove("date");

        JSONArray a = new JSONArray();
        a.put(yyyyFrom);
        a.put(MMFrom);
        a.put(ddFrom);

        REQ.put("dateFrom", a);

        a = new JSONArray();
        a.put(yyyyTo);
        a.put(MMTo);
        a.put(ddTo);

        REQ.put("dateTo", a);
    }

    @API(APILevel = 2)
    public IHWFuture<List<IHWObj>> getFuture() {
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

        result = new ArrayList<IHWObj>();

    }

    @Override
    public IMessageListener getListener() {
        return this;
    }

    //------------------------------------ IMessageListener ------------------------------------------------------------


    @Override
    public void onMessage(JSONObject msg) {
        if (msg.optInt("commID", -1) != cid) return;

        if (msg.optString("handler", "null").equals("de.mlessmann.commands.gethw")) {

            if (msg.optString("payload_type", "null").equals("JSONArray") &&
                    msg.optString("array_type", "null").equals("HWObject")) {

                JSONArray a = msg.getJSONArray("payload");

                ArrayList<IHWObj> list = new ArrayList<IHWObj>();

                //For-Each not possible due to the org.json lib in android
                for (int i = 0; i < a.length(); i++) {
                    Object o = a.get(i);
                    if (o instanceof JSONObject) {
                        JSONObject obj = ((JSONObject) o);

                        list.add(new HWObject(obj));
                    }
                }

                errorCode = IHWFuture.ERRORCodes.OK;
                result = list;
                future.pokeListeners();

            } else if (msg.optString("payload_type", "null").equals("error")) {

                JSONObject e = msg.getJSONObject("payload");

                HWObject o;
                String error = e.optString("error", "null");

                if (error.equals(Errors.ProtoError)) {
                    errorCode = IHWFuture.ERRORCodes.PROTOError;
                    o = HWObject.dummy();
                } else if (error.equals(Errors.LOGINREQError)) {
                    errorCode = IHWFuture.ERRORCodes.LOGINREQ;
                    o = HWObject.dummy();
                } else if (error.equals(Errors.DATETIMEError)) {
                    errorCode = IHWFuture.ERRORCodes.DATETIMEError;
                    o = HWObject.dummy();
                } else {
                    errorCode = IHWFuture.ERRORCodes.UNKNOWN;
                    o = HWObject.dummy();
                }

                ArrayList<IHWObj> list = new ArrayList<IHWObj>();

                list.add(o);
                result = list;
                reqMgr.unregisterListener(this);
                reqMgr.unregisterRequest(this);
                future.pokeListeners();

            }

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
    public List<IHWObj> getPayload(IHWFuture future) {
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
