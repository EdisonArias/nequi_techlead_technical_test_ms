package co.com.nequi.teachlead.technical.test.model.branch.services;

import co.com.nequi.teachlead.technical.test.model.branch.enums.UpdateCounts;
import reactor.core.publisher.Mono;

public interface SyncBranchService {
    Mono<UpdateCounts> updateBranchName(String branchId, String newName);
}

