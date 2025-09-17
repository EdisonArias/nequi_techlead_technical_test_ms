package co.com.nequi.teachlead.technical.test.usecase.branch.update;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.branch.gateway.BranchGateway;
import co.com.nequi.teachlead.technical.test.model.branch.services.SyncBranchService;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.utils.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static co.com.nequi.teachlead.technical.test.usecase.utils.MockData.assertIsIsoInstant;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateBranchUseCaseTest {

    @Mock
    BranchGateway branchGateway;

    @Mock
    SyncBranchService syncBranchService;

    @InjectMocks
    UpdateBranchUseCase useCase;

    @Captor
    ArgumentCaptor<Branch> branchCaptor;

    @Test
    void executeShouldUpdateBranchAndSyncWhenBranchExists() {
        // Arrange
        String branchId = "B1";
        Branch existing = MockData.branch(branchId, "Old Name");
        Branch input = MockData.branch(null, "New Name");

        Branch toSave = existing.toBuilder().name("New Name").build();
        Branch saved = toSave.toBuilder().build();

        given(branchGateway.getBranchById(branchId)).willReturn(Mono.just(existing));
        given(branchGateway.saveBranch(any(Branch.class))).willReturn(Mono.just(saved));
        given(syncBranchService.updateBranchName(branchId, "New Name")).willReturn(Mono.empty());

        // Act
        Mono<Branch> result = useCase.execute(input, branchId);

        // Assert
        StepVerifier.create(result)
                .expectNext(saved)
                .verifyComplete();

        verify(branchGateway).saveBranch(branchCaptor.capture());
        Branch built = branchCaptor.getValue();
        assert "New Name".equals(built.getName());
        assertIsIsoInstant(built.getModificationDate());
    }

    @Test
    void execute_ShouldError_WhenBranchNotFound() {
        // Arrange
        String branchId = "B404";
        Branch input = MockData.branch(null, "Irrelevant");

        given(branchGateway.getBranchById(branchId)).willReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(input, branchId))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getType() == BusinessType.NO_BRANCH_FOUND)
                .verify();
    }

    @Test
    void execute_ShouldPropagateError_WhenSaveFails() {
        // Arrange
        String branchId = "B1";
        Branch existing = MockData.branch(branchId, "Old Name");
        Branch input = MockData.branch(null, "New Name");

        given(branchGateway.getBranchById(branchId)).willReturn(Mono.just(existing));
        given(branchGateway.saveBranch(any(Branch.class)))
                .willReturn(Mono.error(new RuntimeException("DB error on save")));

        // Act & Assert
        StepVerifier.create(useCase.execute(input, branchId))
                .expectErrorMessage("DB error on save")
                .verify();
    }

    @Test
    void execute_ShouldPropagateError_WhenSyncFails() {
        // Arrange
        String branchId = "B1";
        Branch existing = MockData.branch(branchId, "Old Name");
        Branch input = MockData.branch(null, "New Name");
        Branch saved = existing.toBuilder().name("New Name").build();

        given(branchGateway.getBranchById(branchId)).willReturn(Mono.just(existing));
        given(branchGateway.saveBranch(any(Branch.class))).willReturn(Mono.just(saved));
        given(syncBranchService.updateBranchName(branchId, "New Name"))
                .willReturn(Mono.error(new RuntimeException("Sync error")));

        // Act & Assert
        StepVerifier.create(useCase.execute(input, branchId))
                .expectErrorMessage("Sync error")
                .verify();
    }
}
