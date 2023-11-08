package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.pfc;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.AerViolation.AerViolationMessage;
import uk.gov.pmrv.api.reporting.domain.dto.PfcEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.PfcEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationOfPfcEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmission;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.pfc.PfcEmissionsCalculationService;
import uk.gov.pmrv.api.reporting.transform.EmissionCalculationParamsMapper;

@Service
@RequiredArgsConstructor
public class PfcSourceStreamEmissionsValidator implements AerCalculationOfPfcSourceStreamEmissionValidator {

    private final PfcEmissionsCalculationService pfcEmissionsCalculationService;

    private static final EmissionCalculationParamsMapper EMISSION_CALCULATION_PARAMS_MAPPER =
        Mappers.getMapper(EmissionCalculationParamsMapper.class);

    @Override
    public List<AerViolation> validate(PfcSourceStreamEmission pfcSourceStreamEmission,
        AerContainer aerContainer) {
        PfcEmissionsCalculationParamsDTO pfcEmissionsCalculationParamsDTO = EMISSION_CALCULATION_PARAMS_MAPPER.toPfcEmissionsCalculationParamsDTO(
            pfcSourceStreamEmission);

        PfcEmissionsCalculationDTO calculatedEmissions = pfcEmissionsCalculationService.calculateEmissions(pfcEmissionsCalculationParamsDTO);

        PfcEmissionsCalculationDTO pfcEmissionsCalculationDTO =
            EMISSION_CALCULATION_PARAMS_MAPPER.toPfcEmissionsCalculationDTO(pfcSourceStreamEmission);

        List<AerViolation> violations = new ArrayList<>();
        if (!calculatedEmissions.equals(pfcEmissionsCalculationDTO)) {
            violations.add(new AerViolation(CalculationOfPfcEmissions.class.getSimpleName(),
                AerViolationMessage.CALCULATION_PFC_INCORRECT_TOTAL_EMISSIONS));
        }
        return violations;
    }
}
