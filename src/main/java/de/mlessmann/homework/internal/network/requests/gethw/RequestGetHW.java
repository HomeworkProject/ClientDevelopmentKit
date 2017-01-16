package de.mlessmann.homework.internal.network.requests.gethw;


import de.mlessmann.common.annotations.API;
import de.mlessmann.homework.api.error.Error;
import de.mlessmann.homework.api.event.network.CloseReason;
import de.mlessmann.homework.api.future.IHWFuture;
import de.mlessmann.homework.api.homework.IHomework;
import de.mlessmann.homework.internal.CDKConnectionBase;
import de.mlessmann.homework.internal.future.HWFuture;
import de.mlessmann.homework.internal.homework.HWObject;
import de.mlessmann.homework.internal.logging.LogManager;
import de.mlessmann.homework.internal.network.IHWConnListener;
import de.mlessmann.homework.internal.network.requests.Errors;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Life4YourGames on 09.08.16.
 */
public class RequestGetHW implements IHWConnListener {


    static final JSONObject REQ = new JSONObject("{\n\"command\": \"gethw\"\n}");

    private String id;
    private int cid;
    private HWFuture<List<IHomework>> future;
    private CDKConnectionBase conn;
    private LogManager lMgr;

    //------------------------------------------------------------------------------------------------------------------

    public RequestGetHW(LogManager logger, CDKConnectionBase conn) {
        lMgr = logger;
        this.conn = conn;
        this.future = new HWFuture<List<IHomework>>();
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

    public IHWFuture<List<IHomework>> getFuture() {
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

    //------------------------------------ IMessageListener ------------------------------------------------------------


    @Override
    public boolean processJSON(JSONObject msg) {
        if (msg.optInt("commID", -1) != cid) return false;

        if (msg.optString("handler", "null").equals("de.mlessmann.commands.gethw")) {

            if (msg.optString("payload_type", "null").equals("JSONArray") &&
                    msg.optString("array_type", "null").equals("HWObject")) {

                JSONArray a = msg.getJSONArray("payload");

                List<IHomework> list = new ArrayList<IHomework>();

                //For-Each not possible due to the org.json lib in android
                for (int i = 0; i < a.length(); i++) {
                    Object o = a.get(i);
                    if (o instanceof JSONObject) {
                        JSONObject obj = ((JSONObject) o);

                        list.add(new HWObject(obj));
                    }
                }

                future.setError(Error.OK);
                future.setPayload(list);
                conn.unregisterListener(this);
                future.pokeListeners();
                return true;

            } else if (msg.optString("payload_type", "null").equals("error")) {

                JSONObject e = msg.getJSONObject("payload");

                HWObject o;
                String error = e.optString("error", "null");

                if (error.equals(Errors.ProtoError)) {
                    future.setError(Error.of(Error.ErrorCode.BADREQUEST));
                    o = HWObject.dummy();
                } else if (error.equals(Errors.LOGINREQError)) {
                    future.setError(Error.of(Error.ErrorCode.UNAUTHORIZED));
                    o = HWObject.dummy();
                } else if (error.equals(Errors.DATETIMEError)) {
                    future.setError(Error.of(Error.ErrorCode.DATEError));
                    o = HWObject.dummy();
                } else {
                    future.setError(Error.of(Error.ErrorCode.UNKNOWN));
                    o = HWObject.dummy();
                }

                ArrayList<IHomework> list = new ArrayList<IHomework>();

                list.add(o);
                future.setPayload(list);
                conn.unregisterListener(this);
                future.pokeListeners();
                return true;
            }

        }
        return false;
    }
}
