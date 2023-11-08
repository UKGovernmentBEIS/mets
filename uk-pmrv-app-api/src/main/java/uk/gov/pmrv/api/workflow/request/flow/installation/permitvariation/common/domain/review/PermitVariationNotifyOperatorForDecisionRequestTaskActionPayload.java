package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload extends RequestTaskActionPayload {

	@NotNull
	@Valid
	private DecisionNotification decisionNotification;
}
