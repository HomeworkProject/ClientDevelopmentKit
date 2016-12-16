package de.mlessmann.homework.internal;

import de.mlessmann.common.annotations.*;
import de.mlessmann.homework.api.CDK;
import de.mlessmann.homework.api.ICDKConnection;
import de.mlessmann.homework.api.event.ICDKEvent;
import de.mlessmann.homework.api.future.IHWFuture;
import de.mlessmann.homework.api.homework.IHWCarrier;
import de.mlessmann.homework.api.homework.IHomework;
import de.mlessmann.homework.api.homework.IHomeworkAttachment;
import de.mlessmann.homework.api.provider.IHWProvider;
import de.mlessmann.homework.api.session.IHWGroupMapping;
import de.mlessmann.homework.api.session.IHWSession;
import de.mlessmann.homework.api.session.IHWUser;
import de.mlessmann.homework.api.stream.IHWStreamAcceptor;
import de.mlessmann.homework.api.stream.IHWStreamProvider;
import de.mlessmann.homework.internal.event.CDKEvent;

import java.net.SocketAddress;
import java.util.List;

/**
 * Created by Life4YourGames on 15.12.16.
 */
public class CDKConnection extends CDKConnectionBase implements ICDKConnection {

    public CDKConnection(CDK cdk, IHWProvider provider) {
        super(cdk);
        super.setProvider(provider);
    }

    public CDKConnection(CDK cdk, SocketAddress addr, int port, int sslPort) {
        super(cdk);
        super.setProvider(addr, port, sslPort);
    }

    //=== === === === === === === === === === === === === === === === === === === === === === === === === === === ===

    @Nullable
    public IHWProvider getProvider() {
        return null;
    }

    @API
    @NoLogin
    @Parallel
    @NotNull
    public IHWFuture<IHWGroupMapping> getGroups(@Nullable String onlyThisGroup) {
        return null;
    }

    @API
    @NoLogin
    @Parallel
    @NotNull
    public IHWFuture<IHWUser> login(IHWSession session) {
        return null;
    }

    @API
    @NoLogin
    @Parallel
    @NotNull
    public IHWFuture<IHWUser> login(String group, String user, String auth) {
        return null;
    }

    @API
    @Parallel
    @NotNull
    public IHWFuture<Boolean> postHW(IHWCarrier newHW) {
        return null;
    }

    @API
    @Parallel
    @NotNull
    public IHWFuture<Boolean> editHW(IHWCarrier oldHW, IHWCarrier newHW) {
        return null;
    }

    @API
    @Parallel
    @NotNull
    public IHWFuture<Boolean> delHW(IHWCarrier oldHW) {
        return null;
    }

    @API
    @Parallel
    @NotNull
    public IHWFuture<List<IHomework>> getHWOn(int yyyy, int MM, int dd) {
        return null;
    }

    @API
    @Parallel
    @NotNull
    public IHWFuture<List<IHomework>> getHWBetween(int yyyyFrom, int MMFrom, int ddFrom, int yyyyTo, int MMTo, int ddTo) {
        return null;
    }

    //Attachments
    @API
    @Parallel
    @NotNull
    public IHWFuture<Boolean> postHWWebAttachment(IHomeworkAttachment attach) {
        return null;
    }

    @API
    @Parallel
    @NotNull
    public IHWFuture<Boolean> postHWServerAttachment(IHomeworkAttachment attach, IHWStreamProvider provider) {
        return null;
    }

    @API
    @Parallel
    @NotNull
    public IHWFuture<Boolean> getHWServerAttachment(IHomeworkAttachment attach, IHWStreamAcceptor acceptor) {
        return null;
    }

    @Override
    public void fireEvent(ICDKEvent event) {
        ((CDKEvent) event).setConnection(this);
        super.fireEvent(event);
    }
}
