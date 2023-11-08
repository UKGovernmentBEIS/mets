package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.GrantDetermination;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class PermitIssuanceGrantDetermination extends GrantDetermination implements PermitIssuanceDeterminateable {
    
}
