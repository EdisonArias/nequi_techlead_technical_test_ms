package co.com.nequi.teachlead.technical.test.usecase.branch.create;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.branch.gateway.BranchGateway;
import co.com.nequi.teachlead.technical.test.model.franchise.services.AddBranchesFranchiseService;
import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.model.franchise.gateway.FranchiseGateway;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class CreateBranchUseCase {

    private static final Logger log = Logger.getLogger(CreateBranchUseCase.class.getName());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
    private static final ZoneOffset zoneOffset = ZoneOffset.UTC;
    private static final String MESSAGE_BRANCH_CREATED = "Saved Branch: ";

    private final FranchiseGateway franchiseGateway;
    private final BranchGateway branchGateway;
    private final AddBranchesFranchiseService addBranchesFranchiseService;

    public Mono<Franchise> execute(String franchiseId, Branch branch) {

        return checkFranchiseExists(franchiseId)
                .map(exist -> buildBranch(branch))
                .flatMap(branchGateway::saveBranch)
                .flatMap(branchSaved -> addBranchToFranchise(franchiseId,branchSaved))
                .doOnNext(saved -> log.info(MESSAGE_BRANCH_CREATED + saved));
    }

    private Mono<Franchise> checkFranchiseExists(String franchiseId) {
        return franchiseGateway.getFranchiseById(franchiseId)
                .switchIfEmpty(Mono.defer(() ->Mono.error(BusinessType.NO_FRANCHISE_FOUND.build(franchiseId))));
    }

    private Branch buildBranch(Branch info) {
        ZonedDateTime now = LocalDateTime.now().atZone(zoneOffset);
        return Branch.builder()
                .name(info.getName())
                .creationDate(now.format(formatter))
                .modificationDate(now.format(formatter))
                .build();
    }

    private Mono<Franchise> addBranchToFranchise(String franchiseId, Branch branch) {
        return addBranchesFranchiseService.addBranch(franchiseId,branch);
    }
}
