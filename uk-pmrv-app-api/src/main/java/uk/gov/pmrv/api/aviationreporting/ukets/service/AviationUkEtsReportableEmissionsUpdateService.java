package uk.gov.pmrv.api.aviationreporting.ukets.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsEntity;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsSaveParams;
import uk.gov.pmrv.api.aviationreporting.common.repository.AviationReportableEmissionsRepository;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsUpdateService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

@Service
@RequiredArgsConstructor
public class AviationUkEtsReportableEmissionsUpdateService implements AviationReportableEmissionsUpdateService {

    private final AviationReportableEmissionsRepository aviationReportableEmissionsRepository;

    @Override
    public void saveReportableEmissions(AviationReportableEmissionsSaveParams saveParams) {
        aviationReportableEmissionsRepository.findByAccountIdAndYear(saveParams.getAccountId(), saveParams.getYear())
                .ifPresentOrElse(emissionsEntity -> {
                    if(saveParams.isFromDre() || !emissionsEntity.isFromDre()) {
                        emissionsEntity.setFromDre(saveParams.isFromDre());
                        emissionsEntity.setReportableEmissions(saveParams.getReportableEmissions().getReportableEmissions());
                    }
                }, () -> {
                    AviationReportableEmissionsEntity reportableEmissionsEntity =
                            AviationReportableEmissionsEntity.builder()
                                    .accountId(saveParams.getAccountId())
                                    .year(saveParams.getYear())
                                    .reportableEmissions(saveParams.getReportableEmissions().getReportableEmissions())
                                    .isFromDre(saveParams.isFromDre())
                                    .build();
                    aviationReportableEmissionsRepository.save(reportableEmissionsEntity);
                });
    }

    @Override
    public EmissionTradingScheme getEmissionTradingScheme() {
        return EmissionTradingScheme.UK_ETS_AVIATION;
    }
}
