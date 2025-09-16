package co.com.nequi.teachlead.technical.test.api.controller.franchise.create.mapper;

import co.com.nequi.teachlead.technical.test.api.controller.franchise.create.request.CreateAndUpdateFranchise;
import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FranchiseMapper {

    Franchise toEntity(CreateAndUpdateFranchise dto);

}
