package daggerok;

import daggerok.props.GRPCProps;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.io.Serializable;
import java.util.Collections;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

/**
 * http get :8081 name=max emo=SAD
 * http get :8081 name=max emo=HAPPY
 */

@Slf4j
@SpringBootApplication
public class GRPCClientApplication {

  @Data
  @NoArgsConstructor
  static class Message implements Serializable {
    String name;
    Emo emo;
  }

  @Bean
  public RouterFunction<ServerResponse> routes(final GRPCProps props) {
    return RouterFunctions.route(GET("/**"), serverRequest -> ServerResponse.ok().body(
        serverRequest
            .bodyToFlux(Message.class)
            //.map("mapped result: "::concat),
            .map(message -> {

              final ManagedChannel channel = ManagedChannelBuilder.forAddress(props.getAddress(), props.getPort())
                                                                  .usePlaintext(true)
                                                                  .build();

              final ChatServiceGrpc.ChatServiceBlockingStub stub = ChatServiceGrpc.newBlockingStub(channel);
              final ChatServiceResponse response = stub.hi(ChatServiceRequest.newBuilder()
                                                                             .setName(message.name)
                                                                             .setEmo(message.emo)
                                                                             .build());

              return message.emo + " request was sent.\nresponse: '" + response.getGreeting() + "' was received.";
            }), String.class));
  }

  @SneakyThrows
  public static void main(String[] args) {

    new SpringApplicationBuilder(GRPCClientApplication.class)
        .properties(Collections.singletonMap("server.port", "8081"))
        .registerShutdownHook(true)
        .run(args);
  }
}
