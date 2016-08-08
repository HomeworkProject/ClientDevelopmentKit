package de.mlessmann.networking.requests.results;

/**
 * Created by Life4YourGames on 08.08.16.
 */
public interface IHWFutureProvider<T> {

    T getPayload(HWFuture future);

}
