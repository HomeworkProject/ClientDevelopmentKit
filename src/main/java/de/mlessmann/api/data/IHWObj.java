package de.mlessmann.api.data;

import com.sun.istack.internal.Nullable;
import de.mlessmann.api.annotations.API;
import org.json.JSONObject;

/**
 * Created by Life4YourGames on 09.08.16.
 */
public interface IHWObj {

    public static final int LOGINREQ = 401;
    public static final int UNKNOWN = -1;
    public static final int PROTOError = 400;
    public static final int DATETIMEError = 400;
    public static final int OK = 200;

    @API(APILevel = 1)
    int errorCode();

    String id();

    String subject();

    int[] date();

    @Nullable
    String getDescription(boolean fromLongSrc);

    String optDescription(boolean fromLongSrc, String d);

    @API(APILevel = 3)
    JSONObject getJSON();

}
