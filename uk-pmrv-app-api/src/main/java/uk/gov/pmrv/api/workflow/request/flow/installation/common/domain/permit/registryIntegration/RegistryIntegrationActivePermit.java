package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;

import java.util.List;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RegistryIntegrationActivePermit {

    private String permitId;
    private String installationName;
    private String operatorName;
    private Integer firstYearOfReportingObligation;
    private List<RegulatedActivityType> regulatedActivity;

}
