package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamCategory;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Validated
@Service
@RequiredArgsConstructor
public class EmissionsCalculationService {

    private final List<SourceStreamCategoryEmissionsCalculationService> sourceStreamCategoryEmissionsCalculationServices;


    public EmissionsCalculationDTO calculateEmissions(@Valid @NotNull EmissionsCalculationParamsDTO calculationParams) {
        SourceStreamCategory sourceStreamCategory = calculationParams.getSourceStreamType().getCategory();

        return sourceStreamCategoryEmissionsCalculationServices.stream()
            .filter(service -> sourceStreamCategory.equals(service.getSourceStreamCategory()))
            .findFirst()
            .map(service -> service.calculateEmissions(calculationParams))
            .orElseThrow(() -> new BusinessException(MetsErrorCode.AER_EMISSIONS_CALCULATION_FAILED));
    }
}
