package co.com.nequi.teachlead.technical.test.api.controller.product.create.mapper;

import co.com.nequi.teachlead.technical.test.api.controller.franchise.create.request.CreateAndUpdateFranchise;
import co.com.nequi.teachlead.technical.test.api.controller.product.create.request.CreateProduct;
import co.com.nequi.teachlead.technical.test.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductMapper {

    Product toEntity(CreateProduct dto);

}
