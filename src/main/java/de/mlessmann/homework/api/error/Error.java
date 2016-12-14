package de.mlessmann.homework.api.error;

import de.mlessmann.common.annotations.Nullable;

/**
 * Created by Life4YourGames on 14.12.16.
 */
public final class Error {

    public static final Error OK = new Error(ErrorCode.OK, null);

    public static Error of(int errorCode, Object error) {
        if (errorCode == 200) return OK;
        return new Error(errorCode, error);
    }

    private int code;
    private Object err;

    private Error(int errorCode, Object error) {
        this.code = errorCode;
        this.err = error;
    }

    //=== === === === === === === ===

    public int getCode() {
        return code;
    }

    @Nullable
    public Object getExc() {
        return err;
    }

    public boolean hasError() {
        return err != null;
    }

    //=== === === === === === === ===

    public interface ErrorCode {

        public static final int UNKNOWN = -1;
        public static final int OK = 200;

        public static final int BADREQUEST = 400;
        public static final int DATEError = 4001;
        public static final int INVALID = 4002;
        public static final int BADURL = 4003;

        public static final int UNAUTHORIZED = 401;
        public static final int BADLOGIN = 4101;
        public static final int BADTOKEN = 4102;

        public static final int FORBIDDEN = 403;

        public static final int NOTFOUND = 404;

        public static final int LOCKED = 423;

        public static final int UNAVAILABLE = 503;
    }
}
