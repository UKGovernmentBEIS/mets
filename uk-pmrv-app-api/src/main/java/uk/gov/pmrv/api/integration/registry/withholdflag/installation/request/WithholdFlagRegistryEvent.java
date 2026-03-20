package uk.gov.pmrv.api.integration.registry.withholdflag.installation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WithholdFlagRegistryEvent {

    private Long accountId;
    private Boolean withholdFlag;
    private Integer year;
    private String requestId;

}
