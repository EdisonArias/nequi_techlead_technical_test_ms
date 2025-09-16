package co.com.nequi.teachlead.technical.test.model.franchise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BranchTopProduct {
    private String branchId;
    private String branchName;
    private String productId;
    private String productName;
    private Integer stock;
}
