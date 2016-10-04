package de.mlessmann.api.data;

import de.mlessmann.common.annotations.Nullable;
import de.mlessmann.common.parallel.IFutureProvider;

/**
 * Created by Life4YourGames on 08.08.16.
 */
public interface IHWFutureProvider<T>  extends IFutureProvider<T> {

    int getErrorCode(IHWFuture future);

    @Nullable
    Object getError(IHWFuture future);
}
