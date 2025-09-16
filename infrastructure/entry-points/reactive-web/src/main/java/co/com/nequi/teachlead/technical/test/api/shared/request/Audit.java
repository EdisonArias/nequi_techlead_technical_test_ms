package co.com.nequi.teachlead.technical.test.api.shared.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class Audit {
    private String name;
    private String email;
}
