package co.com.nequi.teachlead.technical.test.api.controller.branch.get;

import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.usecase.branch.get.GetBranchesUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetBranchesController {

    private final GetBranchesUseCase getBranchesUseCase;

    private static final String MESSAGE_GET_BRANCHES_SUCCESS = "Successfully retrieved branches {}: {}";

    public Mono<ServerResponse> execute(ServerRequest serverRequest) {
        return getBranchesUseCase.execute()
                .collectList()
                .flatMap(brands -> ServerResponse.ok().bodyValue(Response.build(brands)))
                .doOnSuccess(response -> log.info(MESSAGE_GET_BRANCHES_SUCCESS, response,
                        serverRequest.path()));
    }
}
