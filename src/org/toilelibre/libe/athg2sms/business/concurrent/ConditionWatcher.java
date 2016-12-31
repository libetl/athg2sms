package org.toilelibre.libe.athg2sms.business.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

public class ConditionWatcher {

    public static boolean weAreAskedToStopNowBecauseOfThe(Condition stopMonitor) {
        if (stopMonitor == null)return false;
        synchronized (stopMonitor) {
            try {
                if (stopMonitor.await(1, TimeUnit.MILLISECONDS)) {
                    return true;
                }
            } catch (InterruptedException e) {
            } catch (IllegalMonitorStateException e) {
            }
        }
        return false;
    }
}
