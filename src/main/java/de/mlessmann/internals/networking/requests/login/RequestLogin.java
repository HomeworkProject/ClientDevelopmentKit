package de.mlessmann.internals.networking.requests.login;

import de.mlessmann.api.data.IHWFuture;
import de.mlessmann.api.data.IHWFutureProvider;
import de.mlessmann.api.data.IHWSession;
import de.mlessmann.api.data.IHWUser;
import de.mlessmann.api.networking.Errors;
import de.mlessmann.api.networking.IMessageListener;
import de.mlessmann.api.networking.IRequest;
import de.mlessmann.common.annotations.API;
import de.mlessmann.internals.data.HWFuture;
import de.mlessmann.internals.data.HWSession;
import de.mlessmann.internals.logging.LMgr;
import de.mlessmann.internals.networking.requests.RequestMgr;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Life4YourGames on 09.08.16.
 */
public class RequestLogin implements IRequest, IHWFutureProvider<IHWUser>, IMessageListener {

    private final JSONObject REQ = new JSONObject("{\n\"command\": \"login\",\n \"parameters\": [default, default, default]\n}");

    private String id;
    private int cid;
    private int errorCode = 0;
    private IHWUser result = null;
    private HWFuture<IHWUser> future;
    private RequestMgr reqMgr;
    private LMgr lMgr;

    //------------------------------------------------------------------------------------------------------------------

    public RequestLogin(LMgr logger) {

        lMgr = logger;

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
    public void setToken(String token) {
        JSONObject s = new JSONObject();
        s.put("token", token);
        REQ.put("session", s);
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
                null,
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
    public boolean onMessage(JSONObject msg) {
        if (msg.optInt("commID", -1) != cid) return false;

        if (msg.optString("handler", "null").equals("de.mlessmann.commands.login")) {
            boolean unlock = false;
            HWSession s = null;
            if (msg.getInt("status") == 200) {
                if (msg.has("session")) {
                    JSONObject o = msg.getJSONObject("session");
                    s = new HWSession(o);
                }
                result = new Usr(
                        REQ.getJSONArray("parameters").getString(0),
                        REQ.getJSONArray("parameters").getString(1),
                        s,
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
                            s,
                            IHWFuture.ERRORCodes.INVALIDCREDERR
                    );
                    errorCode = IHWFuture.ERRORCodes.INVALIDCREDERR;

                } else if (e.optString("error", "null").equals(Errors.NotFoundError)) {

                    result = new Usr(
                            REQ.getJSONArray("parameters").getString(0),
                            REQ.getJSONArray("parameters").getString(1),
                            s,
                            IHWFuture.ERRORCodes.NOTFOUNDERR
                    );
                    errorCode = IHWFuture.ERRORCodes.NOTFOUNDERR;

                } else if (msg.getInt("status") == IHWFuture.ERRORCodes.EXPIRED){

                    result = new Usr(
                            REQ.getJSONArray("parameters").getString(0),
                            REQ.getJSONArray("parameters").getString(1),
                            s,
                            IHWFuture.ERRORCodes.EXPIRED
                    );

                } else {

                    result = new Usr(
                            REQ.getJSONArray("parameters").getString(0),
                            REQ.getJSONArray("parameters").getString(1),
                            s,
                            IHWFuture.ERRORCodes.UNKNOWN
                    );
                    errorCode = IHWFuture.ERRORCodes.UNKNOWN;

                }
                unlock = true;
            }
            if (unlock) {
                //reqMgr.unlockQueue(this);
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
        private HWSession session;

        public Usr(String grp, String name, HWSession s, int state) {

            this.grp = grp;
            this.name = name;
            this.session = s;
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
        public IHWSession session() {
            return session;
        }

        public int loginStatus() {
            return state;
        }

    }

}
