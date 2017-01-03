package de.mlessmann.homework.internal.event;

import de.mlessmann.homework.api.event.ICDKConnectionEvent;
import de.mlessmann.homework.api.event.network.InterruptReason;

import java.security.cert.X509Certificate;

/**
 * Created by magnus.lessmann on 16.12.2016.
 */
public class CDKConnX509RejectEvent extends CDKConnInterruptEvent implements ICDKConnectionEvent.Interrupted.X509RejectInterrupt {

    private X509Certificate[] chain;
    private boolean pardon;

    public CDKConnX509RejectEvent(Object sender, InterruptReason reason, X509Certificate[] chain) {
        super(sender, reason);
        this.chain = chain;
        this.pardon = false;
    }

    @Override
    public X509Certificate[] getChain() {
        return chain;
    }

    @Override
    public void exemptOnce(boolean exemptOnce) {
        pardon = exemptOnce;
    }
    public boolean getExemptOnce() {
        return pardon;
    }
}
