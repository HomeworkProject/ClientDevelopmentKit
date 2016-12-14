package de.mlessmann.homework.internal.future;

import de.mlessmann.common.annotations.NotNull;
import de.mlessmann.common.annotations.Nullable;
import de.mlessmann.common.parallel.IFutureListener;
import de.mlessmann.homework.api.error.Error;
import de.mlessmann.homework.api.future.IHWFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Life4YourGames on 14.12.16.
 */
public class HWFuture<T> implements IHWFuture<T> {

    private List<IFutureListener> listeners;
    private Error error;
    private T payload;

    public HWFuture() {
        this.error = null;
        this.payload = null;
        this.listeners = new ArrayList<IFutureListener>();
    }

    public void setError(Error error) {
        this.error = error;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    @Override
    @Nullable
    public Error getError() {
        return error;
    }

    @Override
    public T get() throws NoSuchElementException {
        if (payload==null)
            throw new NoSuchElementException("Future is empty");
        return payload;
    }

    @Override
    public T getOrElse(T def) {
        return payload!=null ? payload : def;
    }

    @Override
    public boolean isPresent() {
        return payload!=null;
    }

    @Override
    public void registerListener(@NotNull IFutureListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    @Override
    public void unregisterListener(@NotNull IFutureListener listener) {
        if (!listeners.contains(listener)) return;
        listeners.remove(listener);
    }

    public void pokeListeners() {
        for (int i = listeners.size(); i>0; i--)
            listeners.get(i-1).onFutureAvailable(this);
    }
}
