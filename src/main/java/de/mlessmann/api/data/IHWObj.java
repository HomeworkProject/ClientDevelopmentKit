package de.mlessmann.api.data;

import de.mlessmann.api.annotations.API;
import de.mlessmann.api.annotations.Nullable;

/**
 * Created by Life4YourGames on 09.08.16.
 */
@API
public interface IHWObj extends IHWCarrier {

    boolean isDummy();

    String id();

    String subject();

    int[] date();

    @Nullable
    String getTitle();

    String optTitle(String def);

    @Nullable
    String getDescription();

    String optDescription(String def);

}
