package co.com.nequi.teachlead.technical.test.mongo.franchise.helper;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class BranchTopProductProjection {
    private ObjectId branchId;
    private String branchName;
    private ObjectId productId;
    private String productName;
    private Integer stock;
}
