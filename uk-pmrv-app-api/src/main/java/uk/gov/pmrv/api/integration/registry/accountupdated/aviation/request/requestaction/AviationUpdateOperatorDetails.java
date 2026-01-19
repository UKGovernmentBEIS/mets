package uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request.requestaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AviationUpdateOperatorDetails {

    private Integer registryId;
    private Integer firstYearOfReportingObligation;
    private String emissionsPlanId;
    private String operatorName;

}