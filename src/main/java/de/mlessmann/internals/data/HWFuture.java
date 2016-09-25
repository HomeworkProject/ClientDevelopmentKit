package de.mlessmann.internals.data;

import de.mlessmann.api.data.IHWFuture;
import de.mlessmann.api.data.IHWFutureListener;
import de.mlessmann.api.data.IHWFutureProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Life4YourGames on 08.08.16.
 */
public class HWFuture<T> implements IHWFuture<T> {

    private IHWFutureProvider<T> provider;
    private List<IHWFutureListener> listeners = new ArrayList<IHWFutureListener>();

    public HWFuture(IHWFutureProvider<T> provider) {

        this.provider = provider;

    }

    public boolean isDone() {
        return isPresent() || getErrorCode() != 0;
    }

    public boolean isPresent() {

        return provider.getPayload(this) != null;

    }

    public int getErrorCode() {
        return provider.getErrorCode(this);
    }

    public Object getError() {
        return provider.getError(this);
    }

    public T get() throws NoSuchElementException {
        if (!isPresent())
            throw new NoSuchElementException();

        return provider.getPayload(this);

    }

    public T getOrElse(T def) {
        if (isPresent())
            return get();
        return def;
    }

    public void registerListener(IHWFutureListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    public void pokeListeners() {
        for (IHWFutureListener l : listeners)
            l.notifyListener(this);
    }

}
