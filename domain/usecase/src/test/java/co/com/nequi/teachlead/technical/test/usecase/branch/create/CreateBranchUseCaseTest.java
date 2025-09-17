package co.com.nequi.teachlead.technical.test.usecase.branch.create;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.branch.gateway.BranchGateway;
import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.model.franchise.gateway.FranchiseGateway;
import co.com.nequi.teachlead.technical.test.model.franchise.services.AddBranchesFranchiseService;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.utils.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import static co.com.nequi.teachlead.technical.test.usecase.utils.MockData.assertIsIsoInstant;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateBranchUseCaseTest {

    @Mock
    FranchiseGateway franchiseGateway;

    @Mock
    BranchGateway branchGateway;

    @Mock
    AddBranchesFranchiseService addBranchesFranchiseService;

    @InjectMocks
    CreateBranchUseCase useCase;

    @Captor
    ArgumentCaptor<Branch> branchCaptor;

    @Test
    void executeShouldCreateBranchAndSaveInFranchise() {
        // Arrange
        String franchiseId = "F1";
        Branch input = MockData.branch(null, "Bogota");
        Franchise existing = MockData.franchise(franchiseId, "Exito");
        Branch savedBranch = input.toBuilder().id("B1").build();
        Franchise updated = existing.toBuilder().build();

        given(franchiseGateway.getFranchiseById(franchiseId)).willReturn(Mono.just(existing));
        given(branchGateway.saveBranch(any(Branch.class))).willReturn(Mono.just(savedBranch));
        given(addBranchesFranchiseService.addBranch(franchiseId, savedBranch)).willReturn(Mono.just(updated));

        // Act
        Mono<Franchise> result = useCase.execute(franchiseId, input);

        // Assert
        StepVerifier.create(result)
                .expectNext(updated)
                .verifyComplete();

        verify(branchGateway).saveBranch(branchCaptor.capture());
        Branch built = branchCaptor.getValue();
        assert "Bogota".equals(built.getName());
        assertIsIsoInstant(built.getCreationDate());
        assertIsIsoInstant(built.getModificationDate());
    }

    @Test
    void executeShouldErrorWhenFranchiseNotFound() {
        // Arrange
        String franchiseId = "F404";
        Branch input = MockData.branch(null, "Tunja");

        given(franchiseGateway.getFranchiseById(franchiseId)).willReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId, input))
                .expectErrorMatches(ex -> ex instanceof BusinessException
                        && ((BusinessException) ex).getType() == BusinessType.NO_FRANCHISE_FOUND)
                .verify();
    }

    @Test
    void executeShouldPropagateErrorWhenSaveBranchFails() {
        // Arrange
        String franchiseId = "F1";
        Branch input = MockData.branch(null, "Medellin");
        Franchise existing = MockData.franchise(franchiseId, "Exito");

        given(franchiseGateway.getFranchiseById(franchiseId)).willReturn(Mono.just(existing));
        given(branchGateway.saveBranch(any(Branch.class)))
                .willReturn(Mono.error(new RuntimeException("DB error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId, input))
                .expectErrorMessage("DB error")
                .verify();
    }

    @Test
    void executeShouldPropagateErrorWhenEmbedInFranchiseFails() {
        // Arrange
        String franchiseId = "F1";
        Branch input = MockData.branch(null, "Cali");
        Franchise existing = MockData.franchise(franchiseId, "Exito");
        Branch saved = input.toBuilder().id("B9").build();

        given(franchiseGateway.getFranchiseById(franchiseId)).willReturn(Mono.just(existing));
        given(branchGateway.saveBranch(any(Branch.class))).willReturn(Mono.just(saved));
        given(addBranchesFranchiseService.addBranch(franchiseId, saved))
                .willReturn(Mono.error(new RuntimeException("Embed error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId, input))
                .expectErrorMessage("Embed error")
                .verify();
    }
}
