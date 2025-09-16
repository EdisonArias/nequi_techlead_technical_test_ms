package co.com.nequi.teachlead.technical.test.api.controller.franchise.get;

import co.com.nequi.teachlead.technical.test.api.shared.enums.Headers;
import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.api.shared.util.ValidateRequest;
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

    private final ValidateRequest validateRequest;
    private final GetFranchisesUseCase getFranchisesUseCase;

    private static final String MESSAGE_GET_FRANCHISES_SUCCESS = "Successfully retrieved franchises {}";

    public Mono<ServerResponse> execute(ServerRequest serverRequest) {
        String messageId = serverRequest.headers().firstHeader(Headers.MESSAGE_ID.getName());
        validateRequest.requireMessageId(messageId);

        return getFranchisesUseCase.execute()
                .collectList()
                .flatMap(brands -> ServerResponse.ok().bodyValue(Response.build(messageId, brands)))
                .doOnSuccess(response -> log.info(MESSAGE_GET_FRANCHISES_SUCCESS, response));
    }
}
