package uk.gov.pmrv.api.aviationreporting.corsia.service;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsEntity;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsSaveParams;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsUpdatedEvent;
import uk.gov.pmrv.api.aviationreporting.common.repository.AviationReportableEmissionsRepository;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsUpdateService;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaTotalReportableEmissions;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

@Service
@RequiredArgsConstructor
public class AviationCorsiaReportableEmissionsUpdateService implements AviationReportableEmissionsUpdateService {

    private final AviationReportableEmissionsRepository aviationReportableEmissionsRepository;
    private final ApplicationEventPublisher publisher;

    @Override
    public void saveReportableEmissions(AviationReportableEmissionsSaveParams saveParams) {
        aviationReportableEmissionsRepository.findByAccountIdAndYear(saveParams.getAccountId(), saveParams.getYear())
                .ifPresentOrElse(emissionsEntity -> {
                    if(saveParams.isFromDre() || !emissionsEntity.isFromDre()) {
                        AviationAerCorsiaTotalReportableEmissions totalReportableEmissions =
                                (AviationAerCorsiaTotalReportableEmissions) saveParams.getReportableEmissions();

                        updateEmissions(emissionsEntity, totalReportableEmissions);
                        emissionsEntity.setFromDre(saveParams.isFromDre());

                        emissionsUpdated(saveParams);
                    }
                }, () -> {
                    AviationAerCorsiaTotalReportableEmissions totalReportableEmissions =
                            (AviationAerCorsiaTotalReportableEmissions) saveParams.getReportableEmissions();
                    AviationReportableEmissionsEntity reportableEmissionsEntity =
                            AviationReportableEmissionsEntity.builder()
                                    .accountId(saveParams.getAccountId())
                                    .year(saveParams.getYear())
                                    .isFromDre(saveParams.isFromDre())
                                    .build();

                    updateEmissions(reportableEmissionsEntity, totalReportableEmissions);

                    aviationReportableEmissionsRepository.save(reportableEmissionsEntity);
                    emissionsUpdated(saveParams);
                });
    }

    private void updateEmissions(AviationReportableEmissionsEntity emissionsEntity,
                                 AviationAerCorsiaTotalReportableEmissions totalReportableEmissions) {


        if (totalReportableEmissions.getReportableEmissions() != null) {
            emissionsEntity.setReportableEmissions(totalReportableEmissions.getReportableEmissions());
        }


        if (totalReportableEmissions.getReportableOffsetEmissions() != null) {
            emissionsEntity.setReportableOffsetEmissions(totalReportableEmissions.getReportableOffsetEmissions());
        }


        if (totalReportableEmissions.getReportableReductionClaimEmissions() != null) {
            emissionsEntity.setReportableReductionClaimEmissions(totalReportableEmissions.getReportableReductionClaimEmissions());
        }
    }
    
    private void emissionsUpdated(AviationReportableEmissionsSaveParams saveParams) {

        AviationAerCorsiaTotalReportableEmissions totalReportableEmissions =
                                    (AviationAerCorsiaTotalReportableEmissions) saveParams.getReportableEmissions();

        if (totalReportableEmissions!= null && totalReportableEmissions.getReportableEmissions() != null) {
            publisher.publishEvent(AviationReportableEmissionsUpdatedEvent.builder()
        		.accountId(saveParams.getAccountId())
        		.isFromDre(saveParams.isFromDre())
        		.reportableEmissions(totalReportableEmissions.getReportableEmissions())
        		.year(saveParams.getYear())
                .isFromRegulator(saveParams.isFromRegulator())
        		.build());
        }

	}

    @Override
    public EmissionTradingScheme getEmissionTradingScheme() {
        return EmissionTradingScheme.CORSIA;
    }
}
