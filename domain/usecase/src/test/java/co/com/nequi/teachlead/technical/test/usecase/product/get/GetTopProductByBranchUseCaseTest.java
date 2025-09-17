package co.com.nequi.teachlead.technical.test.usecase.product.get;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.franchise.BranchTopProduct;
import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.model.franchise.gateway.FranchiseGateway;
import co.com.nequi.teachlead.technical.test.model.franchise.gateway.ProductTopBranchGateway;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessException;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import co.com.nequi.teachlead.technical.test.usecase.utils.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetTopProductByBranchUseCaseTest {

    @Mock
    FranchiseGateway franchiseGateway;

    @Mock
    ProductTopBranchGateway topQueryGateway;

    @InjectMocks
    GetTopProductByBranchUseCase useCase;

    @Test
    void executeShouldReturnTopProductPerBranch() {
        // Arrange
        String franchiseId = "F1";
        Branch b1 = MockData.branch("B1", "Bogota");
        Branch b2 = MockData.branch("B2", "Medellin");
        Franchise franchise = MockData.franchise(franchiseId, "Exito", List.of(b1, b2));

        BranchTopProduct t1 = MockData.top("B1", "Bogota", "P-10", "Zapatos", 50);
        BranchTopProduct t2 = MockData.top("B2", "Medellin", "P-20", "Camisa", 30);

        given(franchiseGateway.getFranchiseById(franchiseId)).willReturn(Mono.just(franchise));
        given(topQueryGateway.getTopProductForBranch("B1")).willReturn(Mono.just(t1));
        given(topQueryGateway.getTopProductForBranch("B2")).willReturn(Mono.just(t2));

        // Act
        Flux<BranchTopProduct> result = useCase.execute(franchiseId);

        // Assert
        StepVerifier.create(result.collectList())
                .expectNextMatches(list ->
                        list.size() == 2 &&
                                list.stream().anyMatch(tp -> tp.getBranchId().equals("B1")
                                        && tp.getProductId().equals("P-10") && tp.getStock() == 50) &&
                                list.stream().anyMatch(tp -> tp.getBranchId().equals("B2")
                                        && tp.getProductId().equals("P-20") && tp.getStock() == 30)
                )
                .verifyComplete();
    }

    @Test
    void executeShouldSkipBranchesWithoutProducts() {
        // Arrange
        String franchiseId = "F2";
        Branch b1 = MockData.branch("B1", "Bogota");
        Branch b2 = MockData.branch("B2", "Medellin");
        Franchise franchise = MockData.franchise(franchiseId, "Falabella", List.of(b1, b2));

        BranchTopProduct t1 = MockData.top("B1", "Bogota", "P-10", "Zapatos", 50);

        given(franchiseGateway.getFranchiseById(franchiseId)).willReturn(Mono.just(franchise));
        given(topQueryGateway.getTopProductForBranch("B1")).willReturn(Mono.just(t1));
        given(topQueryGateway.getTopProductForBranch("B2")).willReturn(Mono.empty());

        // Act
        Flux<BranchTopProduct> result = useCase.execute(franchiseId);

        // Assert
        StepVerifier.create(result.collectList())
                .expectNextMatches(list ->
                        list.size() == 1 &&
                                list.getFirst().getBranchId().equals("B1") &&
                                list.getFirst().getProductId().equals("P-10")
                )
                .verifyComplete();
    }

    @Test
    void executeShouldReturnEmptyListWhenFranchiseHasNoBranches() {
        // Arrange
        String franchiseId = "F3";
        Franchise franchise = MockData.franchise(franchiseId, "PriceSmart", List.of());

        given(franchiseGateway.getFranchiseById(franchiseId)).willReturn(Mono.just(franchise));

        // Act
        Flux<BranchTopProduct> result = useCase.execute(franchiseId);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void executeShouldErrorWhenFranchiseNotFound() {
        // Arrange
        String franchiseId = "only";
        given(franchiseGateway.getFranchiseById(franchiseId)).willReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(useCase.execute(franchiseId))
                .expectErrorMatches(ex -> ex instanceof BusinessException &&
                        ((BusinessException) ex).getType() == BusinessType.NO_FRANCHISE_FOUND)
                .verify();
    }
}
