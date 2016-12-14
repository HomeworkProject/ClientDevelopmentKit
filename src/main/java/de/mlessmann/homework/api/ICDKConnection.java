package de.mlessmann.homework.api;

import de.mlessmann.common.annotations.*;
import de.mlessmann.homework.api.future.IHWFuture;
import de.mlessmann.homework.api.provider.IHWProvider;

/**
 * Created by Life4YourGames on 14.12.16.
 */
public interface ICDKConnection {

    @NotNull
    IHWProvider getProvider();

    @API
    @NoLogin
    @Parallel
    @NotNull
    IHWFuture<IHWGroupMapping> getGroups(@Nullable String onlyThisGroup);

    @API
    @NoLogin
    @Parallel
    @NotNull
    IHWFuture<IHWUser> login(IHWSession session);

    @API
    @NoLogin
    @Parallel
    @NotNull
    IHWFuture<IHWUser> login(String group, String user, String auth);

    @API
    @Parallel
    @NotNull
    IHWFuture<Boolean> postHW(IHWCarrier newHW);

    @API
    @Parallel
    @NotNull
    IHWFuture<Boolean> editHW(IHWCarrier oldHW, IHWCarrier newHW);

    @API
    @Parallel
    @NotNull
    IHWFuture<Boolean> delHW(IHWCarrier oldHW);

    @API
    @Parallel
    @NotNull
    IHWFuture<List<IHomework>> getHWOn(int yyyy, int MM, int dd);

    @API
    @Parallel
    @NotNull
    IHWFuture<List<IHomework>> getHWBetween(int yyyyFrom, int MMFrom, int ddFrom, int yyyyTo, int MMTo, int ddTo);

    //Attachments
    @API
    @Parallel
    @NotNull
    IHWFuture<Boolean> postHWWebAttachment(IHomeworkAttachment attach);

    @API
    @Parallel
    @NotNull
    IHWFuture<Boolean> postHWServerAttachment(IHomeworkAttachment attach, IHWStreamProvider provider);

    @API
    @Parallel
    @NotNull
    IHWFuture<Boolean> getHWServerAttachment(IHomeworkAttachment attach, IHWStreamAcceptor acceptor);
}
