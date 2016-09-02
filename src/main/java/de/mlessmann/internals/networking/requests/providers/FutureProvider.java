package de.mlessmann.internals.networking.requests.providers;

import de.mlessmann.api.data.IHWFuture;
import de.mlessmann.api.data.IHWFutureProvider;
import de.mlessmann.internals.data.HWFuture;

/**
 * Created by Life4YourGames on 02.09.16.
 */
public class FutureProvider<T> implements IHWFutureProvider<T> {

    private T payload;
    private int errorCode = 0;
    private HWFuture<T> future;

    public FutureProvider() {
        future = new HWFuture<T>(this);
    }

    public IHWFuture<T> getFuture() {
        return future;
    }

    @Override
    public int getErrorCode(IHWFuture future) {
        return errorCode;
    }

    @Override
    public T getPayload(IHWFuture future) {
        return payload;
    }

    public void setErrorCode(int code) {
        errorCode = code;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public void pokeListeners() {
        future.pokeListeners();
    }

}
