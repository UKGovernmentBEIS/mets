package uk.gov.pmrv.api.aviationreporting.common.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerContainer;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsEntity;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsSaveParams;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationReportableEmissionsDTO;
import uk.gov.pmrv.api.aviationreporting.common.repository.AviationReportableEmissionsRepository;
import uk.gov.pmrv.api.aviationreporting.common.transform.AviationReportableEmissionsMapper;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AviationReportableEmissionsService {

    private final AviationReportableEmissionsRepository aviationReportableEmissionsRepository;
    private final List<AviationAerReportableEmissionsCalculationService<? extends AviationAerContainer, ? extends AviationAerTotalReportableEmissions>> reportableEmissionsCalculationServices;
    private final List<AviationReportableEmissionsUpdateService> aviationReportableEmissionsUpdateServices;

    private static final AviationReportableEmissionsMapper REPORTABLE_EMISSIONS_MAPPER = Mappers.getMapper(AviationReportableEmissionsMapper.class);

    @Transactional(readOnly = true)
    public Map<Year, AviationReportableEmissionsDTO> getReportableEmissions(Long accountId, Set<Year> years) {
        return aviationReportableEmissionsRepository
            .findAllByAccountIdAndYearIn(accountId, years).stream()
            .collect(Collectors.toMap(AviationReportableEmissionsEntity::getYear, REPORTABLE_EMISSIONS_MAPPER::toAviationReportableEmissionsDTO));
    }
    
    @Transactional
    public AviationAerTotalReportableEmissions updateReportableEmissions(AviationAerContainer aerContainer, Long accountId) {
        Year reportingYear = aerContainer.getReportingYear();
        AviationAerTotalReportableEmissions totalReportableEmissions =
            getReportableEmissionsCalculationService(aerContainer.getScheme()).calculateReportableEmissions(aerContainer);

        AviationReportableEmissionsSaveParams reportableEmissionsSaveParams =
            AviationReportableEmissionsSaveParams.builder()
                .accountId(accountId)
                .year(reportingYear)
                .reportableEmissions(totalReportableEmissions)
                .isFromDre(false)
                .build();

        saveReportableEmissions(reportableEmissionsSaveParams, aerContainer.getScheme());
        return totalReportableEmissions;
    }

    @Transactional
    public void saveReportableEmissions(AviationReportableEmissionsSaveParams saveParams, EmissionTradingScheme emissionTradingScheme) {
        getSaveEmissionsService(emissionTradingScheme).saveReportableEmissions(saveParams);
    }

    @Transactional
    public void updateReportableEmissionsExemptedFlag(Long accountId, Year year, boolean exempted) {
        aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, year)
            .ifPresent(entity -> entity.setExempted(exempted));

    }

    @Transactional
    public void deleteReportableEmissions(Long accountId, Year year) {
        aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, year)
            .ifPresent(aviationReportableEmissionsRepository::delete);
    }

    private AviationAerReportableEmissionsCalculationService getReportableEmissionsCalculationService(EmissionTradingScheme emissionTradingScheme) {
        return reportableEmissionsCalculationServices.stream()
                .filter(emissionsCalculationService -> emissionTradingScheme.equals(emissionsCalculationService.getEmissionTradingScheme()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "No suitable calculation service found"));
    }

    private AviationReportableEmissionsUpdateService getSaveEmissionsService(EmissionTradingScheme emissionTradingScheme) {
        return aviationReportableEmissionsUpdateServices.stream()
                .filter(emissionsCalculationService -> emissionTradingScheme.equals(emissionsCalculationService.getEmissionTradingScheme()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "No suitable save emissions service found"));
    }
}
