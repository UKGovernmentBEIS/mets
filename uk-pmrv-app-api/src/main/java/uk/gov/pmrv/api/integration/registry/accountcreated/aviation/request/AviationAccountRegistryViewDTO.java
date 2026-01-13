package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationOperatorDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationOrganisationDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AviationAccountRegistryViewDTO {

    private AviationOperatorDetails operatorDetails;
    private AviationOrganisationDetails organisationDetails;

}
