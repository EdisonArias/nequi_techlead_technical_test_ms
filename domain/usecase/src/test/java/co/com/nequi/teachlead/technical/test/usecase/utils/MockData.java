package co.com.nequi.teachlead.technical.test.usecase.utils;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import co.com.nequi.teachlead.technical.test.model.franchise.BranchTopProduct;
import co.com.nequi.teachlead.technical.test.model.franchise.Franchise;
import co.com.nequi.teachlead.technical.test.model.product.Product;

import java.time.format.DateTimeParseException;
import java.util.List;

public class MockData {

    public static Franchise franchise(String id, String name, List<Branch> branches) {
        return Franchise.builder()
                .id(id)
                .name(name)
                .branches(branches)
                .creationDate("2025-09-15T00:00:00Z")
                .modificationDate("2025-09-15T00:00:00Z")
                .build();
    }

    public static Franchise franchise(String id, String name) {
        return Franchise.builder()
                .id(id)
                .name(name)
                .creationDate("2025-09-15T00:00:00Z")
                .modificationDate("2025-09-15T00:00:00Z")
                .build();
    }

    public static Franchise franchiseInput(String name) {
        return Franchise.builder()
                .name(name)
                .build();
    }

    public static Branch branch(String id, String name) {
        return Branch.builder()
                .id(id)
                .name(name)
                .build();
    }

    public static BranchTopProduct top(String branchId, String branchName, String productId, String productName, int stock) {
        return BranchTopProduct.builder()
                .branchId(branchId)
                .branchName(branchName)
                .productId(productId)
                .productName(productName)
                .stock(stock)
                .build();
    }

    public static Product productInput(String name, Integer stock) {
        return Product.builder()
                .name(name)
                .stock(stock)
                .build();
    }

    public static Product productSaved(String id, String name, Integer stock) {
        return Product.builder()
                .id(id)
                .name(name)
                .stock(stock)
                .creationDate("2025-09-15T00:00:00Z")
                .modificationDate("2025-09-15T00:00:00Z")
                .build();
    }

    public static void assertIsIsoInstant(String s) {
        try {
            java.time.Instant.parse(s);
        } catch (DateTimeParseException e) {
            throw new AssertionError("Fecha no es ISO_INSTANT: " + s, e);
        }
    }
}
