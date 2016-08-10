package de.mlessmann.api.data;

import de.mlessmann.internals.data.HWFuture;

/**
 * Created by Life4YourGames on 08.08.16.
 */
public interface IHWFutureProvider<T> {

    T getPayload(HWFuture future);

}
