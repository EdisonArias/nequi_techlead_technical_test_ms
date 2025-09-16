package co.com.nequi.teachlead.technical.test.api.controller.product.create.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateProduct {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Stock number of product is required")
    @Min(value = 0, message = "Stock number of product must be a positive number")
    private Integer stock;
}