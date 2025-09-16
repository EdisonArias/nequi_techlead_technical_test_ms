package co.com.nequi.teachlead.technical.test.usecase.branch.update;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.branch.gateway.BranchGateway;
import co.com.nequi.teachlead.technical.test.model.branch.services.SyncBranchService;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class UpdateBranchUseCase {

    private static final Logger log = Logger.getLogger(UpdateBranchUseCase.class.getName());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
    private static final ZoneOffset zoneOffset = ZoneOffset.UTC;
    private static final String MESSAGE_FRANCHISE_UPDATED = "Update Branch: ";

    private final BranchGateway repository;
    private final SyncBranchService syncBranchService;

    public Mono<Branch> execute(Branch newBranch, String branchId) {
        return this.repository.getBranchById(branchId)
                .map(branchFound -> buildUpdateFranchise(branchFound,newBranch.getName()))
                .flatMap(repository::saveBranch)
                .flatMap(saved -> syncBranchInFranchise(saved, newBranch))
                .doOnNext(result -> log.info(MESSAGE_FRANCHISE_UPDATED + result))
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(BusinessType.NO_BRANCH_FOUND.build(branchId))));
    }

    private Branch buildUpdateFranchise(Branch branchNew , String newName) {
        ZonedDateTime now = LocalDateTime.now().atZone(zoneOffset);
        return branchNew.toBuilder()
                .name(newName)
                .modificationDate(now.format(formatter))
                .build();
    }

    private Mono<Branch> syncBranchInFranchise(Branch saved, Branch newBranch) {
        return syncBranchService.updateBranchName(saved.getId(), newBranch.getName())
                .thenReturn(saved);
    }
}
