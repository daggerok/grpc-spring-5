package daggerok;

import daggerok.services.ChatServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@Slf4j
@SpringBootApplication
public class GRPCServerApplication {

  @Bean
  ApplicationRunner runner(@Value("${gRPC.port:8123}") final Integer port) {

    return args -> {
      val server = ServerBuilder.forPort(port)
                                .addService(new ChatServiceImpl())
                                .build()
                                .start();

      Runtime.getRuntime().addShutdownHook(
          new Thread(() -> Optional.ofNullable(server)
                                   .ifPresent(Server::shutdown)));

      log.info("gRPS service listening port {}", port);
      server.awaitTermination();
    };
  }

  public static void main(String[] args) {
    SpringApplication.run(GRPCServerApplication.class, args);
  }
}
