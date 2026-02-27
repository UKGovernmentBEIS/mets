package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.request.requestaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AviationReportableEmissionsRegistryIntegrationRequestActionPayload extends RequestActionPayload {

    private AviationReportableEmissionsOperatorDetails operatorDetails;

}
