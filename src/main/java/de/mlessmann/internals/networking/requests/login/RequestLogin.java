package de.mlessmann.internals.networking.requests.login;

import de.mlessmann.api.annotations.API;
import de.mlessmann.api.data.IHWFuture;
import de.mlessmann.api.data.IHWUser;
import de.mlessmann.api.networking.Errors;
import de.mlessmann.api.networking.IMessageListener;
import de.mlessmann.api.networking.IRequest;
import de.mlessmann.internals.networking.requests.RequestMgr;
import de.mlessmann.internals.data.HWFuture;
import de.mlessmann.api.data.IHWFutureProvider;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Life4YourGames on 09.08.16.
 */
public class RequestLogin implements IRequest, IHWFutureProvider<IHWUser>, IMessageListener {

    static final JSONObject REQ = new JSONObject("{\n\"command\": \"login\",\n \"parameters\": [default, default, default]\n}");

    private String id;
    private int cid;
    private int errorCode = 0;
    private IHWUser result = null;
    private HWFuture<IHWUser> future;
    private RequestMgr reqMgr;

    //------------------------------------------------------------------------------------------------------------------

    public RequestLogin() {

        genID();

        this.future = new HWFuture<IHWUser>(this);

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
        REQ.getJSONArray("parameters").put(0, grp);
    }

    @API(APILevel = 2)
    public void setUsr(String usr) {
        REQ.getJSONArray("parameters").put(1, usr);
    }

    @API(APILevel = 2)
    public void setAuth(String auth) {
        REQ.getJSONArray("parameters").put(2, auth);
    }

    @API(APILevel = 2)
    public IHWFuture<IHWUser> getFuture() {
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
        result = new Usr(
                REQ.getJSONArray("parameters").getString(0),
                REQ.getJSONArray("parameters").getString(1),
                IHWFuture.ERRORCodes.UNKNOWN
        );
        errorCode = IHWFuture.ERRORCodes.UNKNOWN;
    }

    @Override
    public IMessageListener getListener() {
        return this;
    }

    //------------------------------------ IMessageListener ------------------------------------------------------------


    @Override
    public void onMessage(JSONObject msg) {
        if (msg.optString("handler", "null").equals("de.mlessmann.commands.login")) {

            boolean unlock = false;

            if (msg.getInt("status") == 200) {
                result = new Usr(
                        REQ.getJSONArray("paramaters").getString(0),
                        REQ.getJSONArray("paramarers").getString(1),
                        IHWFuture.ERRORCodes.LOGGEDIN
                );
                errorCode = IHWFuture.ERRORCodes.LOGGEDIN;

                unlock = true;
            } else if (msg.optString("payload_type", "null").equals("error")) {

                JSONObject e = msg.getJSONObject("payload");

                if (e.optString("error", "null").equals(Errors.InvCredError)) {
                    result = new Usr(
                            REQ.getJSONArray("parameters").getString(0),
                            REQ.getJSONArray("parameters").getString(1),
                            IHWFuture.ERRORCodes.INVALIDCREDERR
                    );
                    errorCode = IHWFuture.ERRORCodes.INVALIDCREDERR;

                } else if (e.optString("error", "null").equals(Errors.NotFoundError)) {

                    result = new Usr(
                            REQ.getJSONArray("parameters").getString(0),
                            REQ.getJSONArray("parameters").getString(1),
                            IHWFuture.ERRORCodes.NOTFOUNDERR
                    );
                    errorCode = IHWFuture.ERRORCodes.NOTFOUNDERR;

                } else {

                    result = new Usr(
                            REQ.getJSONArray("parameters").getString(0),
                            REQ.getJSONArray("parameters").getString(1),
                            IHWFuture.ERRORCodes.UNKNOWN
                    );
                    errorCode = IHWFuture.ERRORCodes.UNKNOWN;

                }

                unlock = true;

            }

            if (unlock)
                reqMgr.unlockQueue(this);
        }

    }

    @Override
    public void reportMgr(RequestMgr mgr) {
        reqMgr = mgr;
    }

    //------------------------------------ IHWFutureProvider -----------------------------------------------------------


    @Override
    public IHWUser getPayload(IHWFuture future) {
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

    //------------------------------------- Private IHWUser ------------------------------------------------------------

    private class Usr implements IHWUser {

        private String grp;
        private String name;
        private int state;

        public Usr(String grp, String name, int state) {

            this.grp = grp;
            this.name = name;
            this.state = state;

        }

        @Override
        public String group() {
            return grp;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public int loginStatus() {
            return state;
        }

    }

}
