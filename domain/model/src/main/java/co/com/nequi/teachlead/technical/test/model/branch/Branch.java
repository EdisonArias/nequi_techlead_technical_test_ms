package co.com.nequi.teachlead.technical.test.model.branch;

import co.com.nequi.teachlead.technical.test.model.product.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Branch {
    private String id;
    private String name;
    private List<Product> products;
    private String creationDate;
    private String modificationDate;
}
