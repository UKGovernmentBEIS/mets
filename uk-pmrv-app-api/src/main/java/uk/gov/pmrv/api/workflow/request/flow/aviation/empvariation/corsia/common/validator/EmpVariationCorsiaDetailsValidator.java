package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.validator;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;

@Service
@Validated
public class EmpVariationCorsiaDetailsValidator {

	public void validate(@Valid @NotNull EmpVariationCorsiaDetails empVariationCorsiaDetails) {
		//validates empVariationCorsiaDetails
	}
}
