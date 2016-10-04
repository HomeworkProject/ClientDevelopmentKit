package de.mlessmann.api.data;

import de.mlessmann.common.parallel.IFuture;
import de.mlessmann.common.parallel.IFutureListener;

/**
 * Created by Life4YourGames on 12.08.16.
 */
public interface IHWFutureListener extends IFutureListener {

    /**
     * @deprecated use onFutureAvailable
     * @see IFutureListener#onFutureAvailable(IFuture) ;
     */
    @Deprecated
    void notifyListener(IHWFuture future);
}
