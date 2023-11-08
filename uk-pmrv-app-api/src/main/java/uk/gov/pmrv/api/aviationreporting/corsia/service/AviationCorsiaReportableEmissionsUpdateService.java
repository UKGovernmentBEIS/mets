package uk.gov.pmrv.api.aviationreporting.corsia.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsEntity;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsSaveParams;
import uk.gov.pmrv.api.aviationreporting.common.repository.AviationReportableEmissionsRepository;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsUpdateService;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaTotalReportableEmissions;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

@Service
@RequiredArgsConstructor
public class AviationCorsiaReportableEmissionsUpdateService implements AviationReportableEmissionsUpdateService {

    private final AviationReportableEmissionsRepository aviationReportableEmissionsRepository;

    @Override
    public void saveReportableEmissions(AviationReportableEmissionsSaveParams saveParams) {
        aviationReportableEmissionsRepository.findByAccountIdAndYear(saveParams.getAccountId(), saveParams.getYear())
                .ifPresentOrElse(emissionsEntity -> {
                    if(saveParams.isFromDre() || !emissionsEntity.isFromDre()) {
                        AviationAerCorsiaTotalReportableEmissions totalReportableEmissions =
                                (AviationAerCorsiaTotalReportableEmissions) saveParams.getReportableEmissions();
                        emissionsEntity.setFromDre(saveParams.isFromDre());
                        emissionsEntity.setReportableEmissions(totalReportableEmissions.getReportableEmissions());
                        emissionsEntity.setReportableOffsetEmissions(totalReportableEmissions.getReportableOffsetEmissions());
                        emissionsEntity.setReportableReductionClaimEmissions(totalReportableEmissions.getReportableReductionClaimEmissions());
                    }
                }, () -> {
                    AviationAerCorsiaTotalReportableEmissions totalReportableEmissions =
                            (AviationAerCorsiaTotalReportableEmissions) saveParams.getReportableEmissions();
                    AviationReportableEmissionsEntity reportableEmissionsEntity =
                            AviationReportableEmissionsEntity.builder()
                                    .accountId(saveParams.getAccountId())
                                    .year(saveParams.getYear())
                                    .reportableEmissions(totalReportableEmissions.getReportableEmissions())
                                    .reportableOffsetEmissions(totalReportableEmissions.getReportableOffsetEmissions())
                                    .reportableReductionClaimEmissions(totalReportableEmissions.getReportableReductionClaimEmissions())
                                    .isFromDre(saveParams.isFromDre())
                                    .build();
                    aviationReportableEmissionsRepository.save(reportableEmissionsEntity);
                });
    }

    @Override
    public EmissionTradingScheme getEmissionTradingScheme() {
        return EmissionTradingScheme.CORSIA;
    }
}
