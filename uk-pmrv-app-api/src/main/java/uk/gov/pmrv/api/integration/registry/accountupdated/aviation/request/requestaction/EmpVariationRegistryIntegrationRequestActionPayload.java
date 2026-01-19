package uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request.requestaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationOrganisationDetails;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmpVariationRegistryIntegrationRequestActionPayload extends RequestActionPayload {

    private AviationUpdateOperatorDetails operatorDetails;
    private AviationOrganisationDetails organisationDetails;

}
