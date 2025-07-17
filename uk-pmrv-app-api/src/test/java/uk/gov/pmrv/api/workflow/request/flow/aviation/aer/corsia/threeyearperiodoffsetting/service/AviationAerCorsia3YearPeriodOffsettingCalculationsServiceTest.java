package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData;

import java.time.Year;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AviationAerCorsia3YearPeriodOffsettingCalculationsServiceTest {

    @InjectMocks
    private AviationAerCorsia3YearPeriodOffsettingCalculationsService service;

    @Test
    void calculatePeriodOffsettingRequirements_positiveResult_returnIt(){
        Long totalCalculatedAnnualOffsetting = 1000L;
        Long totalCefEmissionsReductions = 900L;

        Long result = service.calculatePeriodOffsettingRequirements(totalCalculatedAnnualOffsetting,totalCefEmissionsReductions);

        assertThat(result).isEqualTo(100);
    }

    @Test
    void calculatePeriodOffsettingRequirements_negativeResult_returnZero(){
        Long totalCalculatedAnnualOffsetting = 900L;
        Long totalCefEmissionsReductions = 1000L;

        Long result = service.calculatePeriodOffsettingRequirements(totalCalculatedAnnualOffsetting,totalCefEmissionsReductions);

        assertThat(result).isEqualTo(0);
    }

    @Test
    void calculateTotalCalculatedAnnualOffsettingFromYearlyOffsettingData(){
        Year year1 = Year.of(2021);
        Year year2 = Year.of(2022);
        Year year3 = Year.of(2023);

        Map<Year, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> yearlyOffsettingData = Map.of(
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
                                .build());

        Long result = service.calculateTotalCalculatedAnnualOffsettingFromYearlyOffsettingData(yearlyOffsettingData);

        assertThat(result).isEqualTo(6000);
    }

    @Test
    void calculateTotalCalculatedAnnualOffsettingFromYearlyOffsettingData_nullCalculatedAnnualOffsettingInYearlyData_returnEmptyOptional(){
        Year year1 = Year.of(2021);
        Year year2 = Year.of(2022);
        Year year3 = Year.of(2023);

        Map<Year, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> yearlyOffsettingData = Map.of(
                        year1, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(1000L)
                                .cefEmissionsReductions(100L)
                                .build(),
                        year2, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .cefEmissionsReductions(200L)
                                .build(),
                        year3, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(3000L)
                                .cefEmissionsReductions(300L)
                                .build());

        Long result = service.calculateTotalCalculatedAnnualOffsettingFromYearlyOffsettingData(yearlyOffsettingData);

        assertThat(result).isEqualTo(4000L);
    }

    @Test
    void calculateTotalCefEmissionsReductionsFromYearlyOffsettingData_nullCalculatedAnnualOffsettingInYearlyData_returnEmptyOptional(){
        Year year1 = Year.of(2021);
        Year year2 = Year.of(2022);
        Year year3 = Year.of(2023);

        Map<Year, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> yearlyOffsettingData = Map.of(
                        year1, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(null)
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
                                .build());

        Long result = service.calculateTotalCalculatedAnnualOffsettingFromYearlyOffsettingData(yearlyOffsettingData);

        assertThat(result).isEqualTo(5000L);
    }

    @Test
    void calculateTotalCefEmissionsReductionsFromYearlyOffsettingData(){
        Year year1 = Year.of(2021);
        Year year2 = Year.of(2022);
        Year year3 = Year.of(2023);

        Map<Year, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> yearlyOffsettingData = Map.of(
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
                                .build());

        Long result = service.calculateTotalCalculatedAnnualOffsettingFromYearlyOffsettingData(yearlyOffsettingData);

        assertThat(result).isEqualTo(6000);
    }
}
