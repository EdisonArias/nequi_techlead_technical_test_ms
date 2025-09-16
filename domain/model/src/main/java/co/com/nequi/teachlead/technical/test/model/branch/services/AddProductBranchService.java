package co.com.nequi.teachlead.technical.test.model.branch.services;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.product.Product;
import reactor.core.publisher.Mono;

public interface AddProductBranchService {
    Mono<Branch> addProduct(String branchId, Product product);
}
