package co.com.nequi.teachlead.technical.test.api.controller.product.update.name.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProductName {

    @NotBlank(message = "Product name is required")
    private String name;
}