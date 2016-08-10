package de.mlessmann.api.data;

import java.util.NoSuchElementException;

/**
 * Created by Life4YourGames on 09.08.16.
 */
public interface IHWFuture<T> {

    boolean isDone();

    boolean isPresent();

    T get() throws NoSuchElementException;

    T getOrElse(T def);

}
