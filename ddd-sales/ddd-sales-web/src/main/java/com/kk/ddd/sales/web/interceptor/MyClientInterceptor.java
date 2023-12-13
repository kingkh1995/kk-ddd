package com.kk.ddd.sales.web.interceptor;

import io.grpc.*;

/**
 * 自定义grpc客户端拦截器<br>
 *
 * @author kingk
 */
public class MyClientInterceptor implements ClientInterceptor {
  @Override
  public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
      MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
    return new ForwardingClientCall.SimpleForwardingClientCall<>(
        next.newCall(method, callOptions)) {

      /**
       * 客户端发起远程调用时触发
       *
       * @param responseListener receives response messages
       * @param headers which can contain extra call metadata, e.g. authentication credentials.
       */
      @Override
      public void start(Listener<RespT> responseListener, Metadata headers) {
        // 添加监听器
        super.start(
            new ForwardingClientCallListener.SimpleForwardingClientCallListener<>(
                responseListener) {

              /**
               * 收到服务端发送的响应头时触发
               *
               * @param headers containing metadata sent by the server at the start of the response.
               */
              @Override
              public void onHeaders(Metadata headers) {
                super.onHeaders(headers);
              }

              /**
               * 收到服务端发送的消息时触发
               *
               * @param message returned by the server
               */
              @Override
              public void onMessage(RespT message) {
                super.onMessage(message);
              }
            },
            headers);
      }

      /**
       * 向服务端发送消息时触发，如果type为流式传输，则会触发多次。
       *
       * @param message message to be sent to the server.
       */
      @Override
      public void sendMessage(ReqT message) {
        super.sendMessage(message);
      }
    };
  }
}
