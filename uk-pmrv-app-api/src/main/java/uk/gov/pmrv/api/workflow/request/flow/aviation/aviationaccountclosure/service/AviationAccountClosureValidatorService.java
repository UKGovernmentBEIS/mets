package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosure;

@Validated
@Service
@RequiredArgsConstructor
public class AviationAccountClosureValidatorService {
	
	public void validateAviationAccountClosureObject(@NotNull @Valid AviationAccountClosure aviationAccountClosure) {
		// validate account closure object on final submit
	}

}
