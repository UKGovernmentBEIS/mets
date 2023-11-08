package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationManualCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerCalculationSourceStreamEmissionsCalculationValidatorTest {

    @InjectMocks
    private AerCalculationSourceStreamEmissionsCalculationValidator emissionsCalculationValidator;

    @Spy
    private ArrayList<AerCalculationSourceStreamParamCalcMethodEmissionsValidator> paramCalcMethodEmissionsValidators;

    @Mock
    private AerCalculationSourceStreamManualEmissionsValidator manualEmissionsValidator;

    @BeforeEach
    void setUp() {
        paramCalcMethodEmissionsValidators.add(manualEmissionsValidator);
    }

    @Test
    void validate() {
        String sourceStreamId = "1";
        SourceStreamType sourceStreamType = SourceStreamType.CERAMICS_SCRUBBING;
        Year reportingYear = Year.of(2022);
        SourceStream sourceStream = SourceStream.builder()
            .id(sourceStreamId)
            .reference("SS1")
            .type(sourceStreamType)
            .description(SourceStreamDescription.SCRAP_TYRES)
            .build();
        CalculationManualCalculationMethod manualCalculationMethod = CalculationManualCalculationMethod.builder()
            .type(CalculationParameterCalculationMethodType.MANUAL)
            .build();
        CalculationSourceStreamEmission sourceStreamEmission = CalculationSourceStreamEmission.builder()
            .sourceStream(sourceStreamId)
            .parameterCalculationMethod(manualCalculationMethod)
            .build();
        AerContainer aerContainer = AerContainer.builder()
            .aer(Aer.builder().sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream)).build()).build())
            .reportingYear(reportingYear)
            .build();

        List<AerViolation> aerViolations = List.of(
            new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(),
                AerViolation.AerViolationMessage.CALCULATION_INCORRECT_TOTAL_EMISSIONS)
        );

        when(manualEmissionsValidator.getParameterCalculationMethodType()).thenReturn(CalculationParameterCalculationMethodType.MANUAL);
        when(manualEmissionsValidator.validate(sourceStreamEmission, sourceStreamType, reportingYear)).thenReturn(aerViolations);

        List<AerViolation> result = emissionsCalculationValidator.validate(sourceStreamEmission, aerContainer);

        assertThat(result).isNotEmpty().containsExactlyElementsOf(aerViolations);
    }
}