package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDre;

@Service
@Validated
public class AviationDreUkEtsValidatorService {

    public void validateAviationDre(@NotNull @Valid AviationDre aviationDre) {}
}