package co.com.nequi.teachlead.technical.test.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductToBranch {
    private String id;
    private String name;
    private Integer stock;
}
