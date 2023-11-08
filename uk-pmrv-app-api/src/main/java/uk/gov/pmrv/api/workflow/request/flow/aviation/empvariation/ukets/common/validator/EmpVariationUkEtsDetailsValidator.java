package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.validator;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.common.domain.EmpVariationUkEtsDetails;

@Service
@Validated
public class EmpVariationUkEtsDetailsValidator {

	public void validate(@Valid @NotNull EmpVariationUkEtsDetails empVariationUkEtsDetails) {};
}
