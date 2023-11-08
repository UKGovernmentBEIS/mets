package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain.PermitVariationSaveApplicationRequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitVariationSaveApplicationRegulatorLedRequestTaskActionPayload
		extends PermitVariationSaveApplicationRequestTaskActionPayload {
	
	@NotNull
	private PermitType permitType;

}
