package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Year;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RegistryIntegrationReportableEmissionsActivePermit extends RegistryIntegrationActivePermit {

    private Integer registryId;
    private String reportableEmissions;
    private Year reportingYear;
}
