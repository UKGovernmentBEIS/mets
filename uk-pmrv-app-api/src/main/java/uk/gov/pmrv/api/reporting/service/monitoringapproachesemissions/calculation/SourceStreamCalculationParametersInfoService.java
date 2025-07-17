package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.reporting.domain.dto.CalculationParameterMeasurementUnits;
import uk.gov.pmrv.api.reporting.domain.dto.SourceStreamCalculationParametersInfo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SourceStreamCalculationParametersInfoService {

    private final List<SourceStreamCategoryEmissionsCalculationService> sourceStreamCategoryEmissionsCalculationServices;

    public SourceStreamCalculationParametersInfo getCalculationParametersInfoBySourceStreamType(SourceStreamType sourceStreamType) {
        SourceStreamCategory sourceStreamCategory = sourceStreamType.getCategory();

        List<CalculationParameterMeasurementUnits> parameterMeasurementUnits = sourceStreamCategoryEmissionsCalculationServices.stream()
            .filter(service -> service.getSourceStreamCategory().equals(sourceStreamCategory))
            .findFirst()
            .map(SourceStreamCategoryEmissionsCalculationService::getValidMeasurementUnitsCombinations)
            .orElseThrow(() -> new BusinessException(MetsErrorCode.INVALID_SOURCE_STREAM_TYPE, sourceStreamType));


        return SourceStreamCalculationParametersInfo.builder()
            .applicableTypes(sourceStreamCategory.getApplicableCalculationParameterTypes())
            .measurementUnitsCombinations(parameterMeasurementUnits)
            .build();
    }
}
