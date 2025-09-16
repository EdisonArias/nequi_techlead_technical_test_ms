package co.com.nequi.teachlead.technical.test.api.shared.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Headers {

    MESSAGE_ID("messageId"),
    FRANCHISE_ID("franchiseId"),
    BRANCH_ID("branchId"),
    PRODUCT_ID("productId");

    private final String name;

}
