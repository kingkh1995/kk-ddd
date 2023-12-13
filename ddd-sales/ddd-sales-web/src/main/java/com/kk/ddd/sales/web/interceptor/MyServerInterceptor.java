package com.kk.ddd.sales.web.interceptor;

import io.grpc.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 自定义grpc服务端拦截器 <br>
 *
 * @author kingk
 */
public class MyServerInterceptor implements ServerInterceptor {
  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
      ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
    var newCall =
        new ForwardingServerCall.SimpleForwardingServerCall<>(call) {

          /**
           * 向客户端发送消息时触发，如果type为流式传输，则会触发多次。
           *
           * @see io.grpc.MethodDescriptor.MethodType
           * @param message response message.
           */
          @Override
          public void sendMessage(RespT message) {
            if (ThreadLocalRandom.current().nextBoolean()) {
              call.close(Status.UNAVAILABLE.withDescription("unavailable"), headers);
            }
            super.sendMessage(message);
          }
        };
    return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(
        next.startCall(newCall, headers)) {

      /**
       * 服务端收到客户端发送的消息时触发，如果type为流式传输，则会触发多次。
       *
       * @param message a received request message.
       */
      @Override
      public void onMessage(ReqT message) {
        super.onMessage(message);
      }

      /** 客户端全部发送完成后触发 */
      @Override
      public void onHalfClose() {
        super.onHalfClose();
      }

      /** 远程调用被取消后触发，保证不会触发onComplete。 */
      @Override
      public void onCancel() {
        super.onCancel();
      }

      /** 远程调用完成后触发，保证不会触发onCancel。 */
      @Override
      public void onComplete() {
        super.onComplete();
      }

      /** 服务端准备完成，可以开始向客户端发送消息。 */
      @Override
      public void onReady() {
        super.onReady();
      }
    };
  }
}
