package uk.gov.pmrv.api.reporting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerEntity;
import uk.gov.pmrv.api.reporting.domain.AerSubmitParams;
import uk.gov.pmrv.api.reporting.domain.ReportableEmissionsSaveParams;
import uk.gov.pmrv.api.reporting.repository.AerRepository;
import uk.gov.pmrv.api.reporting.util.AerIdentifierGenerator;
import uk.gov.pmrv.api.reporting.validation.AerValidatorService;

import java.math.BigDecimal;
import java.time.Year;

@Service
@RequiredArgsConstructor
public class AerService {

    private final ReportableEmissionsService reportableEmissionsService;
    private final ReportableEmissionsCalculationService reportableEmissionsCalculationService;
    private final AerRepository aerRepository;
    private final AerValidatorService aerValidatorService;

    @Transactional
    public BigDecimal submitAer(AerSubmitParams params) {
        AerContainer aerContainer = params.getAerContainer();
        Long accountId = params.getAccountId();
        Year reportingYear = aerContainer.getReportingYear();
        BigDecimal reportableEmissions = updateReportableEmissions(params, true);

        aerContainer.setReportableEmissions(reportableEmissions);
        aerValidatorService.validate(aerContainer, accountId);

        // Save AER to DB
        AerEntity aerEntity = AerEntity.builder()
            .id(AerIdentifierGenerator.generate(accountId, reportingYear.getValue()))
            .aerContainer(aerContainer)
            .accountId(accountId)
            .year(reportingYear)
            .build();
        aerRepository.save(aerEntity);

        return reportableEmissions;
    }

    public BigDecimal updateReportableEmissions(AerSubmitParams params, boolean isFromRegulator) {
        AerContainer aerContainer = params.getAerContainer();
        Long accountId = params.getAccountId();
        Year reportingYear = aerContainer.getReportingYear();
        BigDecimal reportableEmissions = reportableEmissionsCalculationService.calculateYearEmissions(aerContainer);

        ReportableEmissionsSaveParams emissionsParams = ReportableEmissionsSaveParams.builder()
            .accountId(accountId)
            .year(reportingYear)
            .reportableEmissions(reportableEmissions)
            .isFromDre(false)
            .isFromRegulator(isFromRegulator)
            .build();
        reportableEmissionsService.saveReportableEmissions(emissionsParams);
        return reportableEmissions;
    }

    public boolean existsAerByAccountIdAndYear(Long accountId, Year year) {
        return aerRepository.existsByAccountIdAndYear(accountId, year);
    }
}
