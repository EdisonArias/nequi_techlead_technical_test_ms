package co.com.nequi.teachlead.technical.test.api.controller.franchise.create.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAndUpdateFranchise {

    @NotBlank(message = "Franchise name is required")
    private String name;
}