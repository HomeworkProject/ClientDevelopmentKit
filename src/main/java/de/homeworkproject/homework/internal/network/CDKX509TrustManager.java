package de.homeworkproject.homework.internal.network;

import de.homeworkproject.homework.api.event.network.InterruptReason;
import de.homeworkproject.homework.internal.CDKConnectionBase;
import de.homeworkproject.homework.internal.error.CDKCertificateCloseException;
import de.homeworkproject.homework.internal.error.CDKCertificateException;
import de.homeworkproject.homework.internal.event.CDKConnX509RejectEvent;
import de.mlessmann.common.annotations.NotNull;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
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

    public boolean doesTrust(X509Certificate[] chain) {
        boolean trusted = false;
        for (X509Certificate x509Certificate : chain) {
            if (doesTrust(x509Certificate)) {
                //Certificate trusted
                trusted = true;
                break;
            }
        }
        return trusted;
    }

    public boolean doesTrust(X509Certificate cert) {
        boolean trusted = false;
        for (X509Certificate trustedCert : trustedCerts) {
            if (trustedCert.equals(cert)) {
                trusted = true;
                break;
            }
        }
        return trusted;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        throw new CDKCertificateException("Not designed to check client certificates!");
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        if (chain==null || chain.length<1 || authType==null || authType.isEmpty())
            throw new IllegalArgumentException("chain or authType invalid");

        if (doesTrust(chain)) return;
        CDKConnX509RejectEvent event = new CDKConnX509RejectEvent(this, InterruptReason.REJECTING_X509, chain);
        connBase.fireEvent(event);
        //Check if cert has been exempted
        if (event.getExemptOnce()) return;
        if (doesTrust(chain)) return;
        if (event.isCancelled()) throw new CDKCertificateCloseException("Untrusted certificate + Cancel requested!");
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

    public SSLContext createSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext ctx = SSLContext.getDefault();
        ctx.init(null, new TrustManager[]{this}, new SecureRandom());
        return ctx;
    }
}
