package de.mlessmann.api.data;

import de.mlessmann.common.annotations.Nullable;
import de.mlessmann.common.parallel.IFuture;

/**
 * Created by Life4YourGames on 09.08.16.
 */
public interface IHWFuture<T> extends IFuture<T> {

    @Deprecated
    boolean isDone();

    int getErrorCode();

    @Nullable
    Object getError();

    public class ERRORCodes {

        public static final int CLOSED = -2;
        public static final int UNKNOWN = -1;
        public static final int OK = 200;
        public static final int LOGGEDIN = OK;

        public static final int PROTOError = 400;
        public static final int DATETIMEError = 4001;
        public static final int INVALIDPAYLOAD = 4002;

        public static final int LOGINREQ = 401;
        public static final int INVALIDCREDERR = 401;
        //E.g. provided token is expired and needs to be renewed
        public static final int EXPIRED = 4011;

        public static final int INSUFFPERM = 403;
        
        public static final int NOTFOUNDERR = 404;

    }

}
