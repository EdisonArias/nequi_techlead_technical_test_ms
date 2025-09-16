package co.com.nequi.teachlead.technical.test.usecase.product.create;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.branch.gateway.BranchGateway;
import co.com.nequi.teachlead.technical.test.model.branch.services.AddProductBranchService;
import co.com.nequi.teachlead.technical.test.model.product.Product;
import co.com.nequi.teachlead.technical.test.model.product.gateway.ProductGateway;
import co.com.nequi.teachlead.technical.test.model.shared.exception.BusinessType;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class CreateProductUseCase {

    private static final Logger log = Logger.getLogger(CreateProductUseCase.class.getName());
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
    private static final ZoneOffset zoneOffset = ZoneOffset.UTC;
    private static final String MESSAGE_PRODUCT_CREATED = "Saved Product: ";

    private final BranchGateway branchGateway;
    private final ProductGateway productGateway;
    private final AddProductBranchService addProductBranchService;

    public Mono<Branch> execute(String branchId, Product product) {

        return checkBranchExists(branchId)
                .map(exist -> buildProduct(product))
                .flatMap(productGateway::saveProduct)
                .flatMap(branchSaved -> addBranchToFranchise(branchId,branchSaved))
                .doOnNext(saved -> log.info(MESSAGE_PRODUCT_CREATED + saved));
    }

    private Mono<Branch> checkBranchExists(String branchId) {
        return branchGateway.getBranchById(branchId)
                .switchIfEmpty(Mono.error(BusinessType.NO_BRANCH_FOUND.build(branchId)));
    }

    private Product buildProduct(Product data) {
        ZonedDateTime now = LocalDateTime.now().atZone(zoneOffset);
        return Product.builder()
                .name(data.getName())
                .stock(data.getStock())
                .creationDate(now.format(formatter))
                .modificationDate(now.format(formatter))
                .build();
    }

    private Mono<Branch> addBranchToFranchise(String branchId, Product product) {
        return addProductBranchService.addProduct(branchId,product);
    }
}
