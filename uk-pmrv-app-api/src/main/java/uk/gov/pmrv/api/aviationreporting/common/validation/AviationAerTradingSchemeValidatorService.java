package uk.gov.pmrv.api.aviationreporting.common.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerContainer;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

public interface AviationAerTradingSchemeValidatorService<T extends AviationAerContainer> {

    void validate(@Valid @NotNull T aerContainer);

    void validateAer(@Valid @NotNull T aerContainer);

    EmissionTradingScheme getEmissionTradingScheme();
}
