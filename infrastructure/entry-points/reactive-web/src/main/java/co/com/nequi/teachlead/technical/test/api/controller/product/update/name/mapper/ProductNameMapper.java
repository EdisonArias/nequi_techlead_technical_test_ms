package co.com.nequi.teachlead.technical.test.api.controller.product.update.name.mapper;

import co.com.nequi.teachlead.technical.test.api.controller.product.update.name.request.UpdateProductName;
import co.com.nequi.teachlead.technical.test.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductNameMapper {

    Product toEntity(UpdateProductName dto);

}
