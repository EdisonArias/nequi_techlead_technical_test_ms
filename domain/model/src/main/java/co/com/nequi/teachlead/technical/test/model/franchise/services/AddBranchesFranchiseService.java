package co.com.nequi.teachlead.technical.test.model.franchise.services;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import reactor.core.publisher.Mono;

public interface AddBranchesFranchiseService {
    Mono<Franchise> addBranch(String franchiseId, Branch branch);
}
