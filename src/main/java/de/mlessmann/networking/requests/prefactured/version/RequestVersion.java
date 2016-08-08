package de.mlessmann.networking.requests.prefactured.version;

import de.mlessmann.annotations.API;
import de.mlessmann.networking.requests.IMessageListener;
import de.mlessmann.networking.requests.IRequest;

import de.mlessmann.networking.requests.RequestMgr;
import de.mlessmann.networking.requests.results.HWFuture;
import de.mlessmann.networking.requests.results.IHWFutureProvider;
import de.mlessmann.util.Common;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Life4YourGames on 08.08.16.
 * Class implements the complete Version check.
 * Also an example that Request, Listener and Provider can be the same object
 */
public class RequestVersion implements IRequest, IMessageListener, IHWFutureProvider<Boolean>{

    static final JSONObject REQ = new JSONObject("{\n\"command\": \"getInfo\"\n}");

    private String id;
    private int cid;
    private Boolean isCompatible = null;
    private HWFuture<Boolean> future;
    private RequestMgr reqMgr;

    //------------------------------------------------------------------------------------------------------------------

    public RequestVersion() {

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
    public HWFuture<Boolean> getFuture() {
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
    public void onMessage(JSONObject msg) {
        if (msg.optString("handler", "null").equals("de.mlessmann.commands.getProtocolVersion")) {

            String version = msg.getString("protoVersion");
            String currentVersion = API.PROTOVERSION;

            isCompatible = Common.areCompatible(version, currentVersion);

            reqMgr.unlockQueue(this);
        }
    }

    @Override
    public void reportMgr(RequestMgr mgr) {
        reqMgr = mgr;
    }

    //------------------------------------ IHWFutureProvider -----------------------------------------------------------


    @Override
    public Boolean getPayload(HWFuture future) {
        if (future == this.future)
            return isCompatible;
        else
            return null;
    }

}

