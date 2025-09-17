package co.com.nequi.teachlead.technical.test.api.handler.branch;

import co.com.nequi.teachlead.technical.test.api.controller.branch.create.CreateBranchController;
import co.com.nequi.teachlead.technical.test.api.controller.branch.get.GetBranchesController;
import co.com.nequi.teachlead.technical.test.api.controller.branch.update.UpdateBranchController;
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
public class BranchHandler {

    private final CreateBranchController createBranchController;
    private final UpdateBranchController updateBranchController;
    private final GetBranchesController getBranchesController;

    public Mono<ServerResponse> createBranch(ServerRequest request) {
        return handleRequest(request, createBranchController::execute,
                REQUEST_CREATE_BRANCH, RESPONSE_CREATE_BRANCH);
    }

    public Mono<ServerResponse> updateBranch(ServerRequest request) {
        return handleRequest(request, updateBranchController::execute,
                REQUEST_UPDATE_BRANCH, RESPONSE_UPDATE_BRANCH);
    }

    public Mono<ServerResponse> geAllBranches(ServerRequest request) {
        return handleRequest(request, getBranchesController::execute,
                REQUEST_GET_BRANCHES, RESPONSE_GET_BRANCHES);
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
