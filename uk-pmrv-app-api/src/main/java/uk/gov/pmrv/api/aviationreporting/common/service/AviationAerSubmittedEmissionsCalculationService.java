package uk.gov.pmrv.api.aviationreporting.common.service;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerContainer;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerSubmittedEmissions;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

public interface AviationAerSubmittedEmissionsCalculationService<T extends AviationAerContainer, U extends AviationAerSubmittedEmissions> {

    U calculateSubmittedEmissions(T aerContainer);

    EmissionTradingScheme getEmissionTradingScheme();
}
