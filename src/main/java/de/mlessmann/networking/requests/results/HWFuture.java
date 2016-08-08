package de.mlessmann.networking.requests.results;

import java.util.NoSuchElementException;

/**
 * Created by Life4YourGames on 08.08.16.
 */
public class HWFuture<T> {

    private IHWFutureProvider<T> provider;

    public HWFuture(IHWFutureProvider<T> provider) {

        this.provider = provider;

    }

    public boolean isDone() {
        return isPresent();
    }

    public boolean isPresent() {

        return provider.getPayload(this) != null;

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

}
