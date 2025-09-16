package co.com.nequi.teachlead.technical.test.api.controller.product.update.stock.mapper;

import co.com.nequi.teachlead.technical.test.api.controller.product.update.stock.request.UpdateProductStock;
import co.com.nequi.teachlead.technical.test.model.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProductStockMapper {

    Product toEntity(UpdateProductStock dto);

}
