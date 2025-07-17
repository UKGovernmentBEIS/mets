package uk.gov.pmrv.api.reporting.service;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.reporting.domain.InstallationReportableEmissionsUpdatedEvent;
import uk.gov.pmrv.api.reporting.domain.ReportableEmissionsEntity;
import uk.gov.pmrv.api.reporting.domain.ReportableEmissionsSaveParams;
import uk.gov.pmrv.api.reporting.repository.ReportableEmissionsRepository;
import uk.gov.pmrv.api.reporting.transform.AccountEmissionsMapper;

@Service
@RequiredArgsConstructor
public class ReportableEmissionsService {

    private final ReportableEmissionsRepository reportableEmissionsRepository;
    private final ApplicationEventPublisher publisher;
    private static final AccountEmissionsMapper accountEmissionsMapper = Mappers.getMapper(AccountEmissionsMapper.class);

    public Map<Year, BigDecimal> getReportableEmissions(Long accountId, Set<Year> years) {
        
        return reportableEmissionsRepository
                .findAllByAccountIdAndYearIn(accountId, years).stream()
                .collect(Collectors.toMap(ReportableEmissionsEntity::getYear, ReportableEmissionsEntity::getReportableEmissions));
    }

    @Transactional
    public void saveReportableEmissions(ReportableEmissionsSaveParams params) {
        reportableEmissionsRepository.findByAccountIdAndYear(params.getAccountId(), params.getYear())
                .ifPresentOrElse(emissionsEntity -> {
                    if(params.isFromDre() || !emissionsEntity.isFromDre()) {
                        emissionsEntity.setFromDre(params.isFromDre());
                        emissionsEntity.setReportableEmissions(params.getReportableEmissions());
                        emissionsUpdated(params);
                    }
                }, () -> {
                    ReportableEmissionsEntity newEmissionsEntity = accountEmissionsMapper
                            .toReportableEmissionsEntity(params);
                    reportableEmissionsRepository.save(newEmissionsEntity);
                    emissionsUpdated(params);
                });
    }

	private void emissionsUpdated(ReportableEmissionsSaveParams params) {
		publisher.publishEvent(InstallationReportableEmissionsUpdatedEvent.builder()
        		.accountId(params.getAccountId())
        		.isFromDre(params.isFromDre())
        		.reportableEmissions(params.getReportableEmissions())
        		.year(params.getYear())
                .isFromRegulator(params.isFromRegulator())
        		.build());
	}
}
