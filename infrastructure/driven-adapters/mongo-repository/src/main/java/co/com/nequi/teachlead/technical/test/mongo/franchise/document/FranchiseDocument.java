package co.com.nequi.teachlead.technical.test.mongo.franchise.document;

import co.com.nequi.teachlead.technical.test.mongo.branch.document.BranchDocument;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "franchises")
public class FranchiseDocument {

    @Id
    private String id;
    private String name;
    private List<BranchDocument> branches;
    private String creationDate;
    private String modificationDate;
}