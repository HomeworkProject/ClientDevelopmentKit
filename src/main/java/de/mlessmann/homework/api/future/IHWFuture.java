package de.mlessmann.homework.api.future;

import de.mlessmann.common.annotations.Nullable;
import de.mlessmann.common.parallel.IFuture;
import de.mlessmann.homework.api.error.Error;

/**
 * Created by Life4YourGames on 14.12.16.
 */
public interface IHWFuture<T> extends IFuture<T> {

    @Nullable
    Error getError();
}
