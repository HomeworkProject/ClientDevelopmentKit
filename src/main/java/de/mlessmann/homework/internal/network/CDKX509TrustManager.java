package de.mlessmann.homework.internal.network;

import de.mlessmann.common.annotations.NotNull;
import de.mlessmann.homework.api.event.network.InterruptReason;
import de.mlessmann.homework.internal.CDKConnectionBase;
import de.mlessmann.homework.internal.error.CDKCertificateException;
import de.mlessmann.homework.internal.event.CDKConnX509RejectEvent;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by magnus.lessmann on 16.12.2016.
 */
public class CDKX509TrustManager implements X509TrustManager {

    private CDKConnectionBase connBase;
    private List<X509Certificate> trustedCerts;
    private List<X509Certificate> acceptedIssuers;

    public CDKX509TrustManager(@NotNull CDKConnectionBase connBase) {
        this.connBase = connBase;
        this.acceptedIssuers = new ArrayList<X509Certificate>();
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        throw new CDKCertificateException("Not designed to check client certificates!");
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (chain==null || chain.length<1 || authType==null || authType.isEmpty())
            throw new IllegalArgumentException("chain or authType invalid");

        //TODO: Check certificate trust
        CDKConnX509RejectEvent event = new CDKConnX509RejectEvent(this, null, InterruptReason.REJECTING_X509, chain);
        connBase.fireEvent(event);
        //Check if cert has been exempted
        if (event.getExemptOnce()) return;


        //TODO: Recheck certificate trust in case it has been trusted previously
        event = new CDKConnX509RejectEvent(this, null, InterruptReason.REJECTED_X509, chain);
        connBase.fireEvent(event);
        //Check if cert has been exempted
        if (event.getExemptOnce()) return;
        throw new CDKCertificateException("Untrusted certificate");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        //TODO: X509TrustManager#getAcceptedIssuers
        return acceptedIssuers.toArray(new X509Certificate[acceptedIssuers.size()]);
    }

    public synchronized void installAcceptedIssuer(X509Certificate cert) {
        if (!acceptedIssuers.contains(cert)) acceptedIssuers.add(cert);
    }

    public synchronized void removeAcceptedIssuer(X509Certificate cert) {
        acceptedIssuers.remove(cert);
    }

    public synchronized void trustCertificate(X509Certificate cert) {
        if (!trustedCerts.contains(cert)) trustedCerts.add(cert);
    }

    public void trustCertificates(X509Certificate[] certs) {
        for (X509Certificate c : certs)
            trustCertificate(c);
    }

    public synchronized void untrustCertificate(X509Certificate cert) {
        trustedCerts.remove(cert);
    }

    public void untrustCertificates(X509Certificate[] certs) {
        for (X509Certificate c : certs)
            untrustCertificate(c);
    }
}
