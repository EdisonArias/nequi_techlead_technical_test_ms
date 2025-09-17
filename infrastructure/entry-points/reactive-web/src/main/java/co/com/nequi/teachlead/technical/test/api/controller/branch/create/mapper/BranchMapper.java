package co.com.nequi.teachlead.technical.test.api.controller.branch.create.mapper;

import co.com.nequi.teachlead.technical.test.api.controller.franchise.create.request.CreateAndUpdateFranchise;
import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BranchMapper {

    Branch toEntity(CreateAndUpdateFranchise dto);

}
