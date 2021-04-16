package com.kkk.op.support.bean;

import com.kkk.op.support.changeTracking.ThreadLocalAggregateSnapshotContextRecorder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * ThreadLocalAggregateSnapshot移除拦截器
 * @author KaiKoo
 */
public class ThreadLocalAggregateSnapshotRemoveInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {
        ThreadLocalAggregateSnapshotContextRecorder.remove();
    }

}
