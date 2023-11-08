package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeemedWithdrawnDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationDeterminateable;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class PermitVariationDeemedWithdrawnDetermination extends DeemedWithdrawnDetermination
		implements PermitVariationDeterminateable {
}