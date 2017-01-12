package de.mlessmann.homework.internal;

import de.mlessmann.common.annotations.*;
import de.mlessmann.homework.api.CDK;
import de.mlessmann.homework.api.ICDKConnection;
import de.mlessmann.homework.api.event.ICDKEvent;
import de.mlessmann.homework.api.filetransfer.IFTToken;
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
import de.mlessmann.homework.api.stream.IHWStreamReadResult;
import de.mlessmann.homework.api.stream.IHWStreamWriteResult;
import de.mlessmann.homework.internal.event.CDKEvent;
import de.mlessmann.homework.internal.logging.LogManager;
import de.mlessmann.homework.internal.network.CDKX509TrustManager;
import de.mlessmann.homework.internal.network.requests.addhw.RequestAddHW;
import de.mlessmann.homework.internal.network.requests.attachments.GETAttachmentFTRequest;
import de.mlessmann.homework.internal.network.requests.attachments.POSTAttachmentFTRequest;
import de.mlessmann.homework.internal.network.requests.attachments.ReceiveAttachmentRequest;
import de.mlessmann.homework.internal.network.requests.attachments.StoreAttachmentRequest;
import de.mlessmann.homework.internal.network.requests.delhw.RequestDelHW;
import de.mlessmann.homework.internal.network.requests.edithw.RequestEditHW;
import de.mlessmann.homework.internal.network.requests.gethw.RequestGetHW;
import de.mlessmann.homework.internal.network.requests.list.RequestList;
import de.mlessmann.homework.internal.network.requests.login.RequestLogin;
import de.mlessmann.homework.internal.network.requests.version.RequestVersion;

import java.util.List;

/**
 * Created by Life4YourGames on 15.12.16.
 */
public class CDKConnection extends CDKConnectionBase implements ICDKConnection {

    public CDKConnection(CDK cdk, IHWProvider provider) {
        super(cdk);
        super.setProvider(provider);
    }

    public CDKConnection(CDK cdk, IHWProvider provider, boolean includeVersionCheck) {
        super(cdk);
        super.setProvider(provider);
        super.setIncludeVersionCheck(includeVersionCheck);
    }

    public CDKConnection(CDK cdk, String host, int port, int sslPort) {
        super(cdk);
        super.setProvider(host, port, sslPort);
    }

    public CDKConnection(CDK cdk, String host, int port, int sslPort, boolean includeVersionCheck) {
        super(cdk);
        super.setProvider(host, port, sslPort);
        super.setIncludeVersionCheck(includeVersionCheck);
    }

    //=== === === === === === === === === === === === === === === === === === === === === === === === === === === ===

    @Nullable
    public IHWProvider getProvider() {
        return null;
    }

    @API
    @NoLogin
    @NotNull
    @Parallel
    public IHWFuture<Boolean> isCompatible() {
        RequestVersion r = new RequestVersion(getLogManager(), this);
        r.execute();
        return r.getFuture();
    }

    @API
    @NoLogin
    @Parallel
    @NotNull
    public IHWFuture<IHWGroupMapping> getGroups(@Nullable String onlyThisGroup) {
        RequestList r = new RequestList(getLogManager(), this);
        if (onlyThisGroup!=null)
            r.setGrp(onlyThisGroup);
        r.execute();
        return r.getFuture();
    }

    @API
    @NoLogin
    @Parallel
    @NotNull
    public IHWFuture<IHWUser> login(IHWSession session) {
        RequestLogin r = new RequestLogin(getLogManager(), this);
        r.setToken(session.getToken());
        r.execute();
        return r.getFuture();
    }

    @API
    @NoLogin
    @Parallel
    @NotNull
    public IHWFuture<IHWUser> login(String group, String user, String auth) {
        RequestLogin r = new RequestLogin(getLogManager(), this);
        r.setGrp(group);
        r.setUsr(user);
        r.setAuth(auth);
        r.execute();
        return r.getFuture();
    }

    @API
    @Parallel
    @NotNull
    public IHWFuture<Boolean> postHW(IHWCarrier newHW) {
        RequestAddHW r = new RequestAddHW(getLogManager(), this);
        r.setHW(newHW);
        r.execute();
        return r.getFuture();
    }

    @API
    @Parallel
    @NotNull
    public IHWFuture<Boolean> editHW(IHomework oldHW, IHWCarrier newHW) {
        RequestEditHW r = new RequestEditHW(getLogManager(), this);
        r.setDate(oldHW.getDate()[0], oldHW.getDate()[1], oldHW.getDate()[2]);
        r.setID(oldHW.getId());
        r.setHW(newHW);
        r.execute();
        return r.getFuture();
    }

    @API
    @Parallel
    @NotNull
    public IHWFuture<Boolean> delHW(String id, int yyyy, int MM, int dd) {
        RequestDelHW r = new RequestDelHW(getLogManager(), this);
        r.setDate(yyyy, MM, dd);
        r.setID(id);
        r.execute();
        return r.getFuture();
    }

    @API
    @Parallel
    @NotNull
    public IHWFuture<Boolean> delHW(IHomework oldHW) {
        RequestDelHW r = new RequestDelHW(getLogManager(), this);
        r.setDate(oldHW.getDate()[0], oldHW.getDate()[1], oldHW.getDate()[2]);
        r.setID(oldHW.getId());
        r.execute();
        return r.getFuture();
    }

    @API
    @Parallel
    @NotNull
    public IHWFuture<List<IHomework>> getHWOn(int yyyy, int MM, int dd) {
        RequestGetHW r = new RequestGetHW(getLogManager(), this);
        r.setDate(yyyy, MM, dd);
        r.execute();
        return r.getFuture();
    }

    @API
    @Parallel
    @NotNull
    public IHWFuture<List<IHomework>> getHWBetween(int yyyyFrom, int MMFrom, int ddFrom, int yyyyTo, int MMTo, int ddTo) {
        RequestGetHW r = new RequestGetHW(getLogManager(), this);
        r.setDates(yyyyFrom, MMFrom, ddFrom, yyyyTo, MMTo, ddTo);
        r.execute();
        return r.getFuture();
    }

    //Attachments
    /**
     * @deprecated Not yet implemented!
     */
    @Deprecated
    @API
    @Parallel
    //@NotNull
    public IHWFuture<Boolean> postHWWebAttachment(IHomeworkAttachment attach) {
        return null;
    }

    @API
    @Parallel
    @NotNull
    public IHWFuture<IFTToken> reqPostHWServerAttachment(IHomeworkAttachment attach) {
        StoreAttachmentRequest r = new StoreAttachmentRequest(getLogManager(), this);
        r.setLocation(attach);
        r.execute();
        return r.getFuture();
    }

    @API
    @Parallel
    @NotNull
    public IHWFuture<IHWStreamWriteResult> postHWServerAttachment(IHomeworkAttachment attach, IFTToken token, IHWStreamProvider provider) {
        POSTAttachmentFTRequest r = new POSTAttachmentFTRequest(getLogManager(), attach, this);
        r.setToken(token);
        r.setProvider(provider);
        new Thread(r).start();
        return r.getFuture();
    }

    @API
    @Parallel
    @NotNull
    public IHWFuture<IFTToken> reqGetHWServerAttachment(IHomeworkAttachment attach) {
        ReceiveAttachmentRequest r = new ReceiveAttachmentRequest(getLogManager(), this);
        r.setLocation(attach);
        r.execute();
        return r.getFuture();
    }

    @API
    @Parallel
    @NotNull
    public IHWFuture<IHWStreamReadResult> getHWServerAttachment(IHomeworkAttachment attach, IFTToken token, IHWStreamAcceptor acceptor) {
        GETAttachmentFTRequest r = new GETAttachmentFTRequest(getLogManager(), attach, this);
        r.setToken(token);
        r.setAcceptor(acceptor);
        new Thread(r).start();
        return r.getFuture();
    }

    @Override
    public void fireEvent(ICDKEvent event) {
        ((CDKEvent) event).setConnection(this);
        super.fireEvent(event);
    }

    //SSL
    @Override
    public CDKX509TrustManager getTrustManager() {
        return super.getTrustManager();
    }

    public LogManager getLogManager() {
        return super.getCDK().getLogManager();
    }

    //Kill
    @Override
    public void kill() {
        super.kill();
    }

    //Close
    @Override
    public boolean close() {
        return super.close();
    }
}
