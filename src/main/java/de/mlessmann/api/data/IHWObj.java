package de.mlessmann.api.data;

import de.mlessmann.common.annotations.API;
import de.mlessmann.common.annotations.Nullable;

/**
 * Created by Life4YourGames on 09.08.16.
 */
@API
public interface IHWObj extends IHWCarrier {

    boolean isDummy();

    String getId();

    String getSubject();

    int[] getDate();

    @Nullable
    String getTitle();

    String optTitle(String def);

    @Nullable
    String getDescription();

    String optDescription(String def);

}
