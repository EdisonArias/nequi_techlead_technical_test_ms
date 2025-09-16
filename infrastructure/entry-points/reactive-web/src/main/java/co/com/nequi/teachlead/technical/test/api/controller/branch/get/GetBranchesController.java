package co.com.nequi.teachlead.technical.test.api.controller.branch.get;

import co.com.nequi.teachlead.technical.test.api.shared.enums.Headers;
import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.api.shared.util.ValidateRequest;
import co.com.nequi.teachlead.technical.test.usecase.branch.get.GetBranchesUseCase;
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
public class GetBranchesController {

    private final ValidateRequest validateRequest;
    private final GetBranchesUseCase getBranchesUseCase;

    private static final String MESSAGE_GET_BRANCHES_SUCCESS = "Successfully retrieved branches {}";

    public Mono<ServerResponse> execute(ServerRequest serverRequest) {
        String messageId = serverRequest.headers().firstHeader(Headers.MESSAGE_ID.getName());
        validateRequest.requireMessageId(messageId);

        return getBranchesUseCase.execute()
                .collectList()
                .flatMap(brands -> ServerResponse.ok().bodyValue(Response.build(messageId, brands)))
                .doOnSuccess(response -> log.info(MESSAGE_GET_BRANCHES_SUCCESS, response));
    }
}
