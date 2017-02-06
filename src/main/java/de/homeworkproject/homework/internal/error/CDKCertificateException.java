package de.homeworkproject.homework.internal.error;

import java.security.cert.CertificateException;

/**
 * Created by magnus.lessmann on 16.12.2016.
 */
public class CDKCertificateException extends CertificateException {

    public CDKCertificateException(String msg) {
        super(msg);
    }
}
