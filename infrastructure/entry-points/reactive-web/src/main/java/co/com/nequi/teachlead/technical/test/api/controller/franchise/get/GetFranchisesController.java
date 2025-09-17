package co.com.nequi.teachlead.technical.test.api.controller.franchise.get;

import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.usecase.franchise.get.GetFranchisesUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetFranchisesController {

    private final GetFranchisesUseCase getFranchisesUseCase;

    private static final String MESSAGE_GET_FRANCHISES_SUCCESS = "Successfully retrieved franchises {} : {}";

    public Mono<ServerResponse> execute(ServerRequest serverRequest) {
        return getFranchisesUseCase.execute()
                .collectList()
                .flatMap(brands -> ServerResponse.ok().bodyValue(Response.build(brands)))
                .doOnSuccess(response -> log.info(MESSAGE_GET_FRANCHISES_SUCCESS, response,
                        serverRequest.path()));
    }
}
