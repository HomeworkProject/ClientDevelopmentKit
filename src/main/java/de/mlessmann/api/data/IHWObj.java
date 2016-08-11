package de.mlessmann.api.data;

import com.sun.istack.internal.Nullable;
import de.mlessmann.api.annotations.API;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Life4YourGames on 09.08.16.
 */
@API
public interface IHWObj extends IHWCarrier {


    //------------------------- API Level 1 -----------------------------

    static IHWObj.Builder builder() {
        return new IHWObj.Builder();
    }


    boolean isDummy();

    String id();

    String subject();

    int[] date();

    @Nullable
    String getDescription(boolean fromLongSrc);

    String optDescription(boolean fromLongSrc, String d);

}
