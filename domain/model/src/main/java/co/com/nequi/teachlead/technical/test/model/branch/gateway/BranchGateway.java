package co.com.nequi.teachlead.technical.test.model.branch.gateway;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchGateway {
    Mono<Branch> saveBranch(Branch branch);
    Mono<Branch> getBranchById(String brandId);
    Flux<Branch> getAllBranches();
}
