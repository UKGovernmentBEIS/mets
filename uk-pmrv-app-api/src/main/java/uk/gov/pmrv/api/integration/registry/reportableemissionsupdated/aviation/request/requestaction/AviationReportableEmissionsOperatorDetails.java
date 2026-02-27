package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.request.requestaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AviationReportableEmissionsOperatorDetails {

    private Integer registryId;
    private Integer firstYearOfReportingObligation;
    private String emissionsPlanId;
    private String operatorName;
    private String reportableEmissions;
    private Year reportingYear;

}