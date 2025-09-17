package co.com.nequi.teachlead.technical.test.mongo.franchise.mapper;

import co.com.nequi.teachlead.technical.test.model.franchise.BranchTopProduct;
import co.com.nequi.teachlead.technical.test.mongo.franchise.helper.BranchTopProductProjection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BranchTopProductMapper {

    public static BranchTopProduct toDomain(BranchTopProductProjection p) {
        return BranchTopProduct.builder()
                .branchId(hexOrNull(p.getBranchId()))
                .branchName(p.getBranchName())
                .productId(hexOrNull(p.getProductId()))
                .productName(p.getProductName())
                .stock(p.getStock())
                .build();
    }

    private static String hexOrNull(ObjectId id) {
        return id != null ? id.toHexString() : null;
    }
}
