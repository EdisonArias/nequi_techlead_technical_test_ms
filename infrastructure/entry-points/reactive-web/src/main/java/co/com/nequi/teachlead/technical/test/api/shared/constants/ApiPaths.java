package co.com.nequi.teachlead.technical.test.api.shared.constants;

public final class ApiPaths {

    private ApiPaths() {
    }

    private static final String BASE = "/api/v1";

    public static final String FRANCHISES = BASE + "/franchises";

    //Franchises
    public static final String UPDATE_FRANCHISE = FRANCHISES + "/{franchiseId}";

    //Branches
    public static final String BRANCHES_BY_FRANCHISE_ID = FRANCHISES + "/{franchiseId}/branches";
    public static final String UPDATE_BRANCH = FRANCHISES + "/branches/{branchId}";
    public static final String GET_ALL_BRANCHES = FRANCHISES + "/branches";

    //Products
    public static final String CREATE_PRODUCT = FRANCHISES + "/branches/{branchId}/products";
    public static final String UPDATE_PRODUCT_STOCK = FRANCHISES + "/branches/products/{productId}/stock";
    public static final String UPDATE_PRODUCT_NAME = FRANCHISES + "/branches/products/{productId}/name";
    public static final String TOP_PRODUCTS_BY_BRANCH = FRANCHISES + "/{franchiseId}/branches/topProducts";
    public static final String GET_ALL_PRODUCTS = FRANCHISES + "/branches/products";
    public static final String DELETE_PRODUCT = FRANCHISES + "/branches/products/{productId}";
}

