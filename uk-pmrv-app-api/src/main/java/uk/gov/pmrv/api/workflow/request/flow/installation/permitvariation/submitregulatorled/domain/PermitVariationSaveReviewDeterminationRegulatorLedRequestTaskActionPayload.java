package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitVariationSaveReviewDeterminationRegulatorLedRequestTaskActionPayload extends RequestTaskActionPayload {

	@NotNull
    private PermitVariationRegulatorLedGrantDetermination determination;
	
	@Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();
}
