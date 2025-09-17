package co.com.nequi.teachlead.technical.test.usecase.branch.get;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.branch.gateway.BranchGateway;
import co.com.nequi.teachlead.technical.test.usecase.utils.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetBranchesUseCaseTest {

    @Mock
    BranchGateway branchGateway;

    @InjectMocks
    GetBranchesUseCase useCase;

    @Test
    void executeShouldReturnListOfBranches() {
        // Arrange
        List<Branch> branches = List.of(
                MockData.branch("B1", "Bogota"),
                MockData.branch("B2", "Medellin")
        );
        given(branchGateway.getAllBranches()).willReturn(Flux.fromIterable(branches));

        // Act
        Flux<Branch> result = useCase.execute();

        // Assert
        StepVerifier.create(result)
                .expectNextSequence(branches)
                .verifyComplete();
    }

    @Test
    void executeShouldReturnEmptyWhenNoBranches() {
        // Arrange
        given(branchGateway.getAllBranches()).willReturn(Flux.empty());

        // Act
        Flux<Branch> result = useCase.execute();

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void executeShouldPropagateErrorWhenGatewayFails() {
        // Arrange
        given(branchGateway.getAllBranches())
                .willReturn(Flux.error(new RuntimeException("DB error")));

        // Act
        Flux<Branch> result = useCase.execute();

        // Assert
        StepVerifier.create(result)
                .expectErrorMessage("DB error")
                .verify();
    }
}
