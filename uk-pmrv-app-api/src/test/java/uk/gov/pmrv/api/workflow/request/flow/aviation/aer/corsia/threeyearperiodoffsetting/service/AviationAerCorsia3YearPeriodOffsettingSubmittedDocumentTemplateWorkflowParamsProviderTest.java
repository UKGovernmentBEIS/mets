package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AviationAerCorsia3YearPeriodOffsettingSubmittedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private AviationAerCorsia3YearPeriodOffsettingSubmittedDocumentTemplateWorkflowParamsProvider paramsProvider;

    @Test
    void getContextActionType(){
        assertThat(paramsProvider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.AVIATION_AER_CORSIA_3_YEAR_PERIOD_OFFSETTING_SUBMITTED);
    }

    @Test
    void constructParams(){
        String requestId = "requestId";

        Year year1 = Year.of(2021);
        Year year2 = Year.of(2022);
        Year year3 = Year.of(2023);

        Map<Year, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> yearlyOffsettingData = Map.of(
                        year1, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(3000L)
                                .cefEmissionsReductions(300L)
                                .build(),
                        year2, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(2000L)
                                .cefEmissionsReductions(200L)
                                .build(),
                        year3, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(1000L)
                                .cefEmissionsReductions(100L)
                                .build()
                );

        AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData totalYearlyOffsettingData = AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                .builder()
                .calculatedAnnualOffsetting(6000L)
                .cefEmissionsReductions(600L)
                .build();

        AviationAerCorsia3YearPeriodOffsetting aviationAerCorsia3YearPeriodOffsetting = AviationAerCorsia3YearPeriodOffsetting
                .builder()
                .schemeYears(List.of(Year.of(2021),Year.of(2022),Year.of(2023)))
                .yearlyOffsettingData(yearlyOffsettingData)
                .totalYearlyOffsettingData(totalYearlyOffsettingData)
                .periodOffsettingRequirements(5400L)
                .schemeYears(List.of(year1,year2,year3))
                .operatorHaveOffsettingRequirements(true)
                .build();


        AviationAerCorsia3YearPeriodOffsettingRequestPayload requestPayload = AviationAerCorsia3YearPeriodOffsettingRequestPayload
                .builder()
                .aviationAerCorsia3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting)
                .build();

        Map<String, Object> params = paramsProvider.constructParams(requestPayload,requestId);

        Map<String, Object> expectedParams = new HashMap<>();

        expectedParams.put("schemeYear1", year1);
        expectedParams.put("schemeYear2", year2);
        expectedParams.put("schemeYear3", year3);
        expectedParams.put("lastYearOfThePeriodPlusOne", year3.plusYears(1));
        expectedParams.put("lastYearOfThePeriodPlusTwo", year3.plusYears(2));
        expectedParams.put("calculatedAnnualOffsettingYear1", yearlyOffsettingData.get(year1).getCalculatedAnnualOffsetting());
        expectedParams.put("calculatedAnnualOffsettingYear2", yearlyOffsettingData.get(year2).getCalculatedAnnualOffsetting());
        expectedParams.put("calculatedAnnualOffsettingYear3", yearlyOffsettingData.get(year3).getCalculatedAnnualOffsetting());
        expectedParams.put("cefEmissionsReductionsYear1", yearlyOffsettingData.get(year1).getCefEmissionsReductions());
        expectedParams.put("cefEmissionsReductionsYear2", yearlyOffsettingData.get(year2).getCefEmissionsReductions());
        expectedParams.put("cefEmissionsReductionsYear3", yearlyOffsettingData.get(year3).getCefEmissionsReductions());
        expectedParams.put("totalCalculatedAnnualOffsetting", totalYearlyOffsettingData.getCalculatedAnnualOffsetting());
        expectedParams.put("totalCefEmissionsReductions", totalYearlyOffsettingData.getCefEmissionsReductions());
        expectedParams.put("periodOffsettingRequirements", 5400L);
        expectedParams.put("operatorHasOffsettingRequirements", true);

        assertThat(params).containsExactlyEntriesOf(expectedParams);

    }

    @Test
    void constructParamsFromSubmitRequestTaskPayload() {

        String requestId = "requestId";

        Year year1 = Year.of(2021);
        Year year2 = Year.of(2022);
        Year year3 = Year.of(2023);

        Map<Year, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> yearlyOffsettingData = Map.of(
                        year1, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(3000L)
                                .cefEmissionsReductions(300L)
                                .build(),
                        year2, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(2000L)
                                .cefEmissionsReductions(200L)
                                .build(),
                        year3, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(1000L)
                                .cefEmissionsReductions(100L)
                                .build()
                );

        AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData totalYearlyOffsettingData = AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                .builder()
                .calculatedAnnualOffsetting(6000L)
                .cefEmissionsReductions(600L)
                .build();

        AviationAerCorsia3YearPeriodOffsetting aviationAerCorsia3YearPeriodOffsetting = AviationAerCorsia3YearPeriodOffsetting
                .builder()
                .schemeYears(List.of(Year.of(2021),Year.of(2022),Year.of(2023)))
                .yearlyOffsettingData(yearlyOffsettingData)
                .totalYearlyOffsettingData(totalYearlyOffsettingData)
                .periodOffsettingRequirements(5400L)
                .schemeYears(List.of(year1,year2,year3))
                .operatorHaveOffsettingRequirements(true)
                .build();


        AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload requestTasPayload = AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload
                .builder()
                .aviationAerCorsia3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting)
                .build();

        AviationAerCorsia3YearPeriodOffsettingRequestPayload requestPayload = AviationAerCorsia3YearPeriodOffsettingRequestPayload
                .builder()
                .build();

        Map<String, Object> params = paramsProvider.constructParamsFromSubmitRequestTaskPayload(requestTasPayload,requestPayload,requestId);

        Map<String, Object> expectedParams = new HashMap<>();

        expectedParams.put("schemeYear1", year1);
        expectedParams.put("schemeYear2", year2);
        expectedParams.put("schemeYear3", year3);
        expectedParams.put("lastYearOfThePeriodPlusOne", year3.plusYears(1));
        expectedParams.put("lastYearOfThePeriodPlusTwo", year3.plusYears(2));
        expectedParams.put("calculatedAnnualOffsettingYear1", yearlyOffsettingData.get(year1).getCalculatedAnnualOffsetting());
        expectedParams.put("calculatedAnnualOffsettingYear2", yearlyOffsettingData.get(year2).getCalculatedAnnualOffsetting());
        expectedParams.put("calculatedAnnualOffsettingYear3", yearlyOffsettingData.get(year3).getCalculatedAnnualOffsetting());
        expectedParams.put("cefEmissionsReductionsYear1", yearlyOffsettingData.get(year1).getCefEmissionsReductions());
        expectedParams.put("cefEmissionsReductionsYear2", yearlyOffsettingData.get(year2).getCefEmissionsReductions());
        expectedParams.put("cefEmissionsReductionsYear3", yearlyOffsettingData.get(year3).getCefEmissionsReductions());
        expectedParams.put("totalCalculatedAnnualOffsetting", totalYearlyOffsettingData.getCalculatedAnnualOffsetting());
        expectedParams.put("totalCefEmissionsReductions", totalYearlyOffsettingData.getCefEmissionsReductions());
        expectedParams.put("periodOffsettingRequirements", 5400L);
        expectedParams.put("operatorHasOffsettingRequirements", true);

        assertThat(params).containsExactlyEntriesOf(expectedParams);
    }

}
