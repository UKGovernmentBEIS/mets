package uk.gov.pmrv.api.aviationreporting.common.service;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerContainer;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerTotalReportableEmissions;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

public interface AviationAerReportableEmissionsCalculationService <T extends AviationAerContainer, U extends AviationAerTotalReportableEmissions> {

    U calculateReportableEmissions(T aerContainer);

    EmissionTradingScheme getEmissionTradingScheme();
}
