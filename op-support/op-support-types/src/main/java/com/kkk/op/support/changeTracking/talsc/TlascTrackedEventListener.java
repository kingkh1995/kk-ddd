package com.kkk.op.support.changeTracking.talsc;

import org.springframework.context.ApplicationListener;

/**
 *
 * @author KaiKoo
 */
public class TlascTrackedEventListener implements ApplicationListener<TlascTrackedEvent> {

    @Override
    public void onApplicationEvent(TlascTrackedEvent event) {
        TlascHolder.hold(event.getContextThreadLocal());
    }
}
