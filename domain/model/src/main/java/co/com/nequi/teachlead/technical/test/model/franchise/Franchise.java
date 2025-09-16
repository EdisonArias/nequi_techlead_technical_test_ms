package co.com.nequi.teachlead.technical.test.model.franchise;

import co.com.nequi.teachlead.technical.test.model.branch.Branch;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Franchise {
    private String id;
    private String name;
    private List<Branch> branches;
    private String creationDate;
    private String modificationDate;
}
