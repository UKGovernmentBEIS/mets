package uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AviationAccountExemptFlagEvent {

    private Long accountId;
    private Integer registryId;
    private Year year;
    private boolean isExempt;

}
