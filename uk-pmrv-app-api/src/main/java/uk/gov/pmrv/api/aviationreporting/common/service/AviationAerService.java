package uk.gov.pmrv.api.aviationreporting.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerContainer;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerEntity;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerSubmitParams;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.common.repository.AviationAerRepository;
import uk.gov.pmrv.api.aviationreporting.common.util.AviationAerIdentifierGenerator;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerValidatorService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AviationAerService {

    private final AviationAerRepository aerRepository;
    private final AviationAerValidatorService aerValidatorService;
    private final AviationReportableEmissionsService reportableEmissionsService;
    private final List<AviationAerSubmittedEmissionsCalculationService<? extends AviationAerContainer, ? extends AviationAerSubmittedEmissions>> emissionsCalculationServices;

    @Transactional
    public Optional<AviationAerTotalReportableEmissions> submitAer(AviationAerSubmitParams params) {
        AviationAerContainer aerContainer = params.getAerContainer();
        Long accountId = params.getAccountId();
        Year reportingYear = aerContainer.getReportingYear();

        AviationAerTotalReportableEmissions reportableEmissions = null;

        if(Boolean.TRUE.equals(aerContainer.getReportingRequired())) {
            reportableEmissions = reportableEmissionsService.updateReportableEmissions(aerContainer, accountId);
            aerContainer.setReportableEmissions(reportableEmissions);
            aerContainer.setSubmittedEmissions(getEmissionsCalculationService(aerContainer.getScheme()).calculateSubmittedEmissions(aerContainer));

            aerValidatorService.validate(accountId, aerContainer);

            // Save AER to DB
            AviationAerEntity aerEntity = AviationAerEntity.builder()
                .id(AviationAerIdentifierGenerator.generate(accountId, reportingYear.getValue()))
                .aerContainer(aerContainer)
                .accountId(accountId)
                .year(reportingYear)
                .build();
            aerRepository.save(aerEntity);
        }

        return Optional.ofNullable(reportableEmissions);
    }

    public boolean existsAerByAccountIdAndYear(Long accountId, Year year) {
        return aerRepository.existsByAccountIdAndYear(accountId, year);
    }

    private AviationAerSubmittedEmissionsCalculationService getEmissionsCalculationService(EmissionTradingScheme emissionTradingScheme) {
        return emissionsCalculationServices.stream()
            .filter(emissionsCalculationService -> emissionTradingScheme.equals(emissionsCalculationService.getEmissionTradingScheme()))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "No suitable calculation service found"));
    }
}
