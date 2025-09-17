package co.com.nequi.teachlead.technical.test.api.controller.franchise.create;

import co.com.nequi.teachlead.technical.test.api.controller.franchise.create.mapper.FranchiseMapper;
import co.com.nequi.teachlead.technical.test.api.controller.franchise.create.request.CreateAndUpdateFranchise;
import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.franchise.create.CreateFranchiseUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateFranchiseController {

    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final FranchiseMapper franchiseMapper;

    public Mono<ServerResponse> execute(ServerRequest serverRequest) {

        return serverRequest.bodyToMono(CreateAndUpdateFranchise.class)
                .switchIfEmpty(Mono.error(BusinessType.BAD_REQUEST.build()))
                .map(franchiseMapper::toEntity)
                .flatMap(createFranchiseUseCase::execute)
                .map(Response::build)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

}
