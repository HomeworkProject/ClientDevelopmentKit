package de.mlessmann.internals.data;

import de.mlessmann.api.data.IHWFuture;
import de.mlessmann.api.data.IHWFutureProvider;
import de.mlessmann.common.parallel.IFutureListener;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Life4YourGames on 08.08.16.
 */
public class HWFuture<T> implements IHWFuture<T> {

    private IHWFutureProvider<T> provider;
    private List<IFutureListener> listeners = new ArrayList<IFutureListener>();

    public HWFuture(IHWFutureProvider<T> provider) {

        this.provider = provider;

    }

    @Override
    public boolean isDone() {
        return isPresent() || getErrorCode() != 0;
    }

    @Override
    public boolean isPresent() {
        return provider.getPayload(this) != null;
    }

    @Override
    public int getErrorCode() {
        return provider.getErrorCode(this);
    }

    @Override
    public Object getError() {
        return provider.getError(this);
    }

    @Override
    public T get() throws NoSuchElementException {
        if (!isPresent())
            throw new NoSuchElementException();

        return provider.getPayload(this);

    }

    @Override
    public T getOrElse(T def) {
        if (isPresent())
            return get();
        return def;
    }

    @Override
    public void registerListener(IFutureListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    @Override
    public void unregisterListener(IFutureListener listener) {
        if (!listeners.contains(listener)) return;
        listeners.remove(listener);
    }


    public void pokeListeners() {
        for (IFutureListener l : listeners)
            l.onFutureAvailable(this);
    }

}
