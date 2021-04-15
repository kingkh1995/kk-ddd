package com.kkk.op.support.changeTracking.talsc;

import com.kkk.op.support.base.Aggregate;
import com.kkk.op.support.marker.Identifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author KaiKoo
 */
public class TlascHolder {

    private final static ThreadLocal<Set<ThreadLocal<Map<? extends Identifier, ? extends Aggregate>>>> HOLDER = ThreadLocal
            .withInitial(HashSet::new);

    public static void hold(
            ThreadLocal<Map<? extends Identifier, ? extends Aggregate>> threadLocal) {
        HOLDER.get().add(threadLocal);
    }

    public static void remove() {
        HOLDER.get().forEach(ThreadLocal::remove);
        HOLDER.remove();
    }

}
