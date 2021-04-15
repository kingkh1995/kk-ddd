package com.kkk.op.support.changeTracking.talsc;

import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.marker.Identifier;
import java.util.Map;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 *
 * @author KaiKoo
 */
public class TlascTrackedEvent<T extends Aggregate<ID>, ID extends Identifier> extends
        ApplicationEvent {

    @Getter
    private final ThreadLocal<Map<ID, T>> contextThreadLocal;

    /**
     * Create a new {@code ApplicationEvent}.
     * @param source the object on which the event initially occurred or with
     * which the event is associated (never {@code null})
     */
    public TlascTrackedEvent(Object source, ThreadLocal<Map<ID, T>> contextThreadLocal) {
        super(source);
        this.contextThreadLocal = contextThreadLocal;
    }
}
