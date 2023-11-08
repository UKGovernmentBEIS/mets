package uk.gov.pmrv.api.aviationreporting.common.service;

import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsSaveParams;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

public interface AviationReportableEmissionsUpdateService {

    @Transactional
    void saveReportableEmissions(AviationReportableEmissionsSaveParams saveParams);

    EmissionTradingScheme getEmissionTradingScheme();
}
