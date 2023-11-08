package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.pfc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.dto.PfcEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.PfcEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.SlopeSourceStreamEmissionCalculationMethodData;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.pfc.PfcEmissionsCalculationService;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PfcSourceStreamEmissionsValidatorTest {

    @Mock
    private PfcEmissionsCalculationService pfcEmissionsCalculationService;

    @InjectMocks
    private PfcSourceStreamEmissionsValidator validator;

    @Test
    void validate() {
        PfcSourceStreamEmission pfcSourceStreamEmission = PfcSourceStreamEmission.builder()
            .totalPrimaryAluminium(BigDecimal.ONE)
            .amountOfCF4(BigDecimal.ONE)
            .amountOfC2F6(BigDecimal.ONE)
            .totalC2F6Emissions(BigDecimal.ONE)
            .totalCF4Emissions(BigDecimal.ONE)
            .reportableEmissions(BigDecimal.ONE)
            .pfcSourceStreamEmissionCalculationMethodData(SlopeSourceStreamEmissionCalculationMethodData.builder()
                .calculationMethod(PFCCalculationMethod.SLOPE)
                .slopeCF4EmissionFactor(BigDecimal.ONE)
                .anodeEffectsPerCellDay(BigDecimal.ONE)
                .averageDurationOfAnodeEffectsInMinutes(BigDecimal.ONE)
                .percentageOfCollectionEfficiency(BigDecimal.ONE)
                .c2F6WeightFraction(BigDecimal.ONE)
                .build())
            .build();

        PfcEmissionsCalculationDTO pfcEmissionsCalculationDTO = PfcEmissionsCalculationDTO.builder()
            .totalC2F6Emissions(BigDecimal.ONE)
            .totalCF4Emissions(BigDecimal.ONE)
            .amountOfC2F6(BigDecimal.ONE)
            .amountOfCF4(BigDecimal.ONE)
            .reportableEmissions(BigDecimal.ONE)
            .build();

        when(pfcEmissionsCalculationService.calculateEmissions(any(PfcEmissionsCalculationParamsDTO.class)))
            .thenReturn(pfcEmissionsCalculationDTO);

        List<AerViolation> aerViolations = validator.validate(pfcSourceStreamEmission,
            AerContainer.builder().build());

        assertThat(aerViolations.size()).isEqualTo(0);

    }

    @Test
    void validate_violationWhenNotEqual() {
        PfcSourceStreamEmission pfcSourceStreamEmission = PfcSourceStreamEmission.builder()
            .pfcSourceStreamEmissionCalculationMethodData(SlopeSourceStreamEmissionCalculationMethodData.builder()
                .calculationMethod(PFCCalculationMethod.SLOPE)
                .slopeCF4EmissionFactor(BigDecimal.TEN)
                .anodeEffectsPerCellDay(BigDecimal.TEN)
                .averageDurationOfAnodeEffectsInMinutes(BigDecimal.TEN)
                .percentageOfCollectionEfficiency(BigDecimal.TEN)
                .c2F6WeightFraction(BigDecimal.TEN)
                .build())
            .build();

        PfcEmissionsCalculationDTO pfcEmissionsCalculationDTO = PfcEmissionsCalculationDTO.builder()
            .totalC2F6Emissions(BigDecimal.ONE)
            .totalCF4Emissions(BigDecimal.ONE)
            .amountOfC2F6(BigDecimal.ONE)
            .amountOfCF4(BigDecimal.ONE)
            .reportableEmissions(BigDecimal.ONE)
            .build();

        when(pfcEmissionsCalculationService.calculateEmissions(any(PfcEmissionsCalculationParamsDTO.class)))
            .thenReturn(pfcEmissionsCalculationDTO);

        List<AerViolation> aerViolations = validator.validate(pfcSourceStreamEmission,
            AerContainer.builder().build());

        assertThat(aerViolations.size()).isEqualTo(1);
    }

}
