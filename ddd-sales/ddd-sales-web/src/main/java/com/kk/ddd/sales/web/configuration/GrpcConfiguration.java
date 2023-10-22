package com.kk.ddd.sales.web.configuration;

import com.kk.ddd.support.annotation.LiteConfiguration;
import com.kk.ddd.support.util.ApplicationContextAwareSingleton;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * <br>
 *
 * @author KaiKoo
 */
@Slf4j
@LiteConfiguration
public class GrpcConfiguration extends ApplicationContextAwareSingleton implements DisposableBean {

  @Value("${grpc.port:9595}")
  private int port;

  private Server server;

  @Override
  public void afterSingletonsInstantiated() {
    ServerBuilder<?> serverBuilder = ServerBuilder.forPort(port);
    this.getApplicationContext()
        .getBeansOfType(BindableService.class)
        .values()
        .forEach(serverBuilder::addService);
    server = serverBuilder.build();
    try {
      server.start();
      log.info("Grpc server started at [{}]", server.getPort());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void destroy() throws Exception {
    server.shutdown();
  }
}
