package co.com.nequi.teachlead.technical.test.usecase.branch.get;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.branch.gateway.BranchGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetBranchesUseCase {

    private final BranchGateway repository;

    public Flux<Branch> execute() {
        return repository.getAllBranches();
    }
}
