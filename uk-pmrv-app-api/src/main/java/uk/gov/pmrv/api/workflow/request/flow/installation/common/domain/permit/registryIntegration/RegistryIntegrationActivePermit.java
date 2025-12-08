package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistryIntegrationActivePermit {

    private String emitterId;
    private String permitId;
    private String installationName;
    private String operatorName;
    private String regulator;
    private LocalDate regulatedActivitiesStartDate;

}
