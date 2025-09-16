package co.com.nequi.teachlead.technical.test.api.controller.franchise.update;

import co.com.nequi.teachlead.technical.test.api.controller.franchise.create.mapper.FranchiseMapper;
import co.com.nequi.teachlead.technical.test.api.controller.franchise.create.request.CreateAndUpdateFranchise;
import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.api.shared.util.ValidateRequest;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.franchise.update.UpdateFranchiseUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static co.com.nequi.teachlead.technical.test.api.shared.enums.Headers.FRANCHISE_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateFranchiseController {

    private final ValidateRequest validateRequest;
    private final UpdateFranchiseUseCase updateFranchiseUseCase;
    private final FranchiseMapper franchiseMapper;

    public Mono<ServerResponse> execute(ServerRequest serverRequest) {
        String franchiseId = serverRequest.pathVariable(FRANCHISE_ID.getName());
        validateRequest.requireFranchiseId(franchiseId);

        return serverRequest.bodyToMono(CreateAndUpdateFranchise.class)
                .switchIfEmpty(Mono.error(BusinessType.BAD_REQUEST.build()))
                .map(franchiseMapper::toEntity)
                .flatMap(newFranchiseName -> updateFranchiseUseCase.execute(newFranchiseName,franchiseId))
                .map(franchise -> Response.build(franchiseId, franchise))
                .flatMap(ServerResponse.ok()::bodyValue);
    }

}
