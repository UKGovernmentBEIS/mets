package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationDetails;

@Service
@Validated
public class PermitVariationDetailsValidator {

	public void validate(@Valid @NotNull PermitVariationDetails permitVariationDetails) {};
}
