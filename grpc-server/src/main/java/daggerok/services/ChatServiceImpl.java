package daggerok.services;

import daggerok.ChatServiceGrpc;
import daggerok.ChatServiceRequest;
import daggerok.ChatServiceResponse;
import daggerok.Emo;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import static java.lang.String.format;

@Slf4j
public class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {

  @Override
  public void hi(final ChatServiceRequest request, final StreamObserver<ChatServiceResponse> responseObserver) {
    // super.hi(request, responseObserver);
    val name = request.getName();
    val message = Emo.SAD == request.getEmo()
        ? format("poor %s baby :(", name)
        : format("hola, %s! (:", name);
    val response = ChatServiceResponse.newBuilder()
                                      .setGreeting(message)
                                      .build();
    responseObserver.onNext(response);
    log.info("\nrequest '{}' received.\nresponse '{}' sent.", request, response);
    responseObserver.onCompleted();
  }
}
