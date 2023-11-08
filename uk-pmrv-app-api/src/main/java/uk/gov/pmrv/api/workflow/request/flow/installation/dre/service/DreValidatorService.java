package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.Dre;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class DreValidatorService {

	public void validateDre(@NotNull @Valid Dre dre) {}
}
