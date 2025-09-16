package co.com.nequi.teachlead.technical.test.mongo.product.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "products")
public class ProductDocument {

    @Id
    private String id;
    private String name;
    private Integer stock;
    private String creationDate;
    private String modificationDate;
}