package co.com.nequi.teachlead.technical.test.api.handler.franchise;

import co.com.nequi.teachlead.technical.test.api.controller.franchise.create.CreateFranchiseController;
import co.com.nequi.teachlead.technical.test.api.controller.franchise.get.GetFranchisesController;
import co.com.nequi.teachlead.technical.test.api.controller.franchise.update.UpdateFranchiseController;
import co.com.nequi.teachlead.technical.test.api.shared.enums.Headers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static co.com.nequi.teachlead.technical.test.api.shared.constants.OperationMessages.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class FranchiseHandler {

    private final CreateFranchiseController createFranchiseController;
    private final UpdateFranchiseController updateFranchiseController;
    private final GetFranchisesController getFranchisesController;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return handleRequest(request, createFranchiseController::execute,
                REQUEST_CREATE_FRANCHISE, RESPONSE_CREATE_FRANCHISE);
    }

    public Mono<ServerResponse> updateFranchise(ServerRequest request) {
        return handleRequest(request, updateFranchiseController::execute,
                REQUEST_UPDATE_FRANCHISE, RESPONSE_UPDATE_FRANCHISE);
    }

    public Mono<ServerResponse> getAllFranchises(ServerRequest request) {
        return handleRequest(request, getFranchisesController::execute,
                REQUEST_GET_FRANCHISES, RESPONSE_GET_FRANCHISES);
    }

    private Mono<ServerResponse> handleRequest(
            ServerRequest request, Function<ServerRequest, Mono<ServerResponse>> controller,
            String logRequest, String logResponse) {
        String messageId = request.headers().firstHeader(Headers.MESSAGE_ID.getName());
        return Mono.just(request)
                .flatMap(controller)
                .doFirst(() -> log.info(logRequest, request, messageId))
                .doOnSuccess(response -> log.info(logResponse, response, messageId));
    }

}
