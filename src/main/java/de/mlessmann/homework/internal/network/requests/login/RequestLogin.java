package de.mlessmann.homework.internal.network.requests.login;

import de.mlessmann.common.annotations.API;
import de.mlessmann.homework.api.error.Error;
import de.mlessmann.homework.api.event.network.CloseReason;
import de.mlessmann.homework.api.future.IHWFuture;
import de.mlessmann.homework.api.session.IHWSession;
import de.mlessmann.homework.api.session.IHWUser;
import de.mlessmann.homework.internal.CDKConnectionBase;
import de.mlessmann.homework.internal.future.HWFuture;
import de.mlessmann.homework.internal.homework.HWSession;
import de.mlessmann.homework.internal.logging.LogManager;
import de.mlessmann.homework.internal.network.IHWConnListener;
import de.mlessmann.homework.internal.network.requests.Errors;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Life4YourGames on 09.08.16.
 */
public class RequestLogin implements IHWConnListener {

    private final JSONObject REQ = new JSONObject("{\n\"command\": \"login\",\n \"parameters\": [default, default, default]\n}");

    private String id;
    private int cid;
    private HWFuture<IHWUser> future;
    private CDKConnectionBase conn;
    private LogManager lMgr;

    //------------------------------------------------------------------------------------------------------------------

    public RequestLogin(LogManager logger, CDKConnectionBase conn) {
        lMgr = logger;
        this.conn = conn;
        this.future = new HWFuture<IHWUser>();
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

    public IHWFuture<IHWUser> getFuture() {
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

    @Override
    public boolean processJSON(JSONObject msg) {
        if (msg.optInt("commID", -1) != cid) return false;

        if (msg.optString("handler", "null").equals("de.mlessmann.commands.login")) {
            HWSession s = null;
            boolean unlock = false;
            int status = msg.optInt("status", 0);
            if (status == 0) return false;

            if (status == 200) {
                if (msg.has("session")) {
                    JSONObject o = msg.getJSONObject("session");
                    s = new HWSession(o);
                }
                future.setPayload(new Usr(
                        REQ.getJSONArray("parameters").getString(0),
                        REQ.getJSONArray("parameters").getString(1),
                        s,
                        Error.ErrorCode.OK
                ));
                future.setError(Error.OK);
                unlock = true;
            } else if (msg.optString("payload_type", "null").equals("error")) {
                JSONObject e = msg.getJSONObject("payload");
                if (e.optString("error", "null").equals(Errors.InvCredError)) {
                    future.setPayload(new Usr(
                            REQ.getJSONArray("parameters").getString(0),
                            REQ.getJSONArray("parameters").getString(1),
                            s,
                            Error.ErrorCode.BADLOGIN
                    ));
                    future.setError(Error.of(Error.ErrorCode.BADLOGIN));

                } else if (e.optString("error", "null").equals(Errors.NotFoundError)) {

                    future.setPayload(new Usr(
                            REQ.getJSONArray("parameters").getString(0),
                            REQ.getJSONArray("parameters").getString(1),
                            s,
                            Error.ErrorCode.NOTFOUND
                    ));
                    future.setError(Error.of(Error.ErrorCode.NOTFOUND));

                } else if (status == 4011){
                    future.setPayload(new Usr(
                            REQ.getJSONArray("parameters").getString(0),
                            REQ.getJSONArray("parameters").getString(1),
                            s,
                            Error.ErrorCode.BADTOKEN
                    ));
                    future.setError(Error.of(Error.ErrorCode.BADTOKEN));

                } else {
                    future.setPayload(new Usr(
                            REQ.getJSONArray("parameters").getString(0),
                            REQ.getJSONArray("parameters").getString(1),
                            s,
                            Error.ErrorCode.UNKNOWN
                    ));
                    future.setError(Error.of(Error.ErrorCode.UNKNOWN));

                }
                unlock = true;
            }
            if (unlock) {
                //reqMgr.unlockQueue(this);
                conn.unregisterListener(this);
                future.pokeListeners();
                return true;
            }
        }
        return false;
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
