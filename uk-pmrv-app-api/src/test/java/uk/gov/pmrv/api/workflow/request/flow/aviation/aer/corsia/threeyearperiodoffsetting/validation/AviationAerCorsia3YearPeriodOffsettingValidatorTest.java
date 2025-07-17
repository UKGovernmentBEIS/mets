package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.validation;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service.AviationAerCorsia3YearPeriodOffsettingCalculationsService;

import java.time.Year;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AviationAerCorsia3YearPeriodOffsettingValidatorTest {


    @InjectMocks
    private AviationAerCorsia3YearPeriodOffsettingValidator validator;

    @Mock
    private AviationAerCorsia3YearPeriodOffsettingCalculationsService calculationsService;


    @Test
    void validate3YearPeriodOffsetting_wrongTotalCalculatedAnnualOffsetting_ThrowBusinessException() {
        Year year1 = Year.of(2021);
        Year year2 = Year.of(2022);
        Year year3 = Year.of(2023);

        AviationAerCorsia3YearPeriodOffsetting aviationAerCorsia3YearPeriodOffsetting = AviationAerCorsia3YearPeriodOffsetting
                .builder()
                .schemeYears(List.of(year1,year2, year3))
                .yearlyOffsettingData(Map.of(
                        year1, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(1000L)
                                .cefEmissionsReductions(100L)
                                .build(),
                        year2, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(2000L)
                                .cefEmissionsReductions(200L)
                                .build(),
                        year3, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(3000L)
                                .cefEmissionsReductions(300L)
                                .build()))
                .totalYearlyOffsettingData(AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                        .builder()
                        .calculatedAnnualOffsetting(6001L)
                        .cefEmissionsReductions(600L)
                        .build())
                .build();

        final BusinessException be = assertThrows(BusinessException.class,() -> {
            validator.validate3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting);
        });

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validate3YearPeriodOffsetting_wrongCefEmissionsReductions_ThrowBusinessException() {
        Year year1 = Year.of(2021);
        Year year2 = Year.of(2022);
        Year year3 = Year.of(2023);

        AviationAerCorsia3YearPeriodOffsetting aviationAerCorsia3YearPeriodOffsetting = AviationAerCorsia3YearPeriodOffsetting
                .builder()
                .schemeYears(List.of(year1,year2, year3))
                .yearlyOffsettingData(Map.of(
                        year1, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(1000L)
                                .cefEmissionsReductions(100L)
                                .build(),
                        year2, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(2000L)
                                .cefEmissionsReductions(200L)
                                .build(),
                        year3, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(3000L)
                                .cefEmissionsReductions(300L)
                                .build()))
                .totalYearlyOffsettingData(AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                        .builder()
                        .calculatedAnnualOffsetting(6000L)
                        .cefEmissionsReductions(601L)
                        .build())
                .build();

        final BusinessException be = assertThrows(BusinessException.class,() -> {
            validator.validate3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting);
        });

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }

    @Test
    void validate3YearPeriodOffsetting_wrongPeriodOffsettingRequirements_ThrowBusinessException() {
        Year year1 = Year.of(2021);
        Year year2 = Year.of(2022);
        Year year3 = Year.of(2023);

        AviationAerCorsia3YearPeriodOffsetting aviationAerCorsia3YearPeriodOffsetting = AviationAerCorsia3YearPeriodOffsetting
                .builder()
                .schemeYears(List.of(year1,year2, year3))
                .yearlyOffsettingData(Map.of(
                        year1, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(1000L)
                                .cefEmissionsReductions(100L)
                                .build(),
                        year2, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(2000L)
                                .cefEmissionsReductions(200L)
                                .build(),
                        year3, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(3000L)
                                .cefEmissionsReductions(300L)
                                .build()))
                .totalYearlyOffsettingData(AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                        .builder()
                        .calculatedAnnualOffsetting(6000L)
                        .cefEmissionsReductions(600L)
                        .build())
                .periodOffsettingRequirements(5401L)
                .build();

        final BusinessException be = assertThrows(BusinessException.class,() -> {
            validator.validate3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting);
        });

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
    }
}
