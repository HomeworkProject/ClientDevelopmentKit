package de.homeworkproject.homework.api.future;

import de.homeworkproject.homework.api.error.Error;
import de.mlessmann.common.annotations.Nullable;
import de.mlessmann.common.parallel.IFuture;

/**
 * Created by Life4YourGames on 14.12.16.
 */
public interface IHWFuture<T> extends IFuture<T> {

    @Nullable
    Error getError();
}
