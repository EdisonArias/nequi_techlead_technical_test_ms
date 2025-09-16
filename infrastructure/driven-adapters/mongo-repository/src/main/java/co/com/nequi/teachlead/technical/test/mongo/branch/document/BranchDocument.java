package co.com.nequi.teachlead.technical.test.mongo.branch.document;

import co.com.nequi.teachlead.technical.test.model.product.Product;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "branches")
public class BranchDocument {

    @Id
    private String id;
    private String name;
    private List<Product> products;
    private String creationDate;
    private String modificationDate;
}