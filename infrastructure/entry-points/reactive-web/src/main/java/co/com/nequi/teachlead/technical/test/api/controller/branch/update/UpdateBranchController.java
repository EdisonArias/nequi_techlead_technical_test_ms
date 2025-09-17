package co.com.nequi.teachlead.technical.test.api.controller.branch.update;

import co.com.nequi.teachlead.technical.test.api.controller.branch.create.mapper.BranchMapper;
import co.com.nequi.teachlead.technical.test.api.controller.franchise.create.request.CreateAndUpdateFranchise;
import co.com.nequi.teachlead.technical.test.api.shared.response.Response;
import co.com.nequi.teachlead.technical.test.api.shared.util.ValidateRequest;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.branch.update.UpdateBranchUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static co.com.nequi.teachlead.technical.test.api.shared.enums.Headers.BRANCH_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateBranchController {

    private final ValidateRequest validateRequest;
    private final UpdateBranchUseCase updateBranchUseCase;
    private final BranchMapper branchMapper;

    public Mono<ServerResponse> execute(ServerRequest serverRequest) {
        String branchId = serverRequest.pathVariable(BRANCH_ID.getName());
        validateRequest.requireFranchiseId(serverRequest.pathVariable(BRANCH_ID.getName()));

        return serverRequest.bodyToMono(CreateAndUpdateFranchise.class)
                .switchIfEmpty(Mono.error(BusinessType.BAD_REQUEST.build()))
                .map(branchMapper::toEntity)
                .flatMap(newBranchName -> updateBranchUseCase.execute(newBranchName,branchId))
                .map(Response::build)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

}
