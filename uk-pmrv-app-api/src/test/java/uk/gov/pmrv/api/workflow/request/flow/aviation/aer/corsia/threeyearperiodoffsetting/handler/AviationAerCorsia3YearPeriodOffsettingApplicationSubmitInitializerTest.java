package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaTotalEmissions;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerRequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service.AviationAerCorsiaAnnualOffsettingRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service.AviationAerCorsia3YearPeriodOffsettingCalculationsService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.math.BigDecimal;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationAerCorsia3YearPeriodOffsettingApplicationSubmitInitializerTest {

    @InjectMocks
    private AviationAerCorsia3YearPeriodOffsettingApplicationSubmitInitializer initializer;

    @Mock
    private RequestService requestService;

    @Mock
    private AviationAerRequestIdGenerator aviationAerRequestIdGenerator;

    @Mock
    private AviationAerCorsiaAnnualOffsettingRequestQueryService annualOffsettingRequestQueryService;

    @Mock
    private AviationAerRequestQueryService aviationAerRequestQueryService;

    @Spy
    private AviationAerCorsia3YearPeriodOffsettingCalculationsService calculationsService;

    @Test
    void initializePayload() {

        final Long accountId = 175L;
        final String aerRequestId = "AEM00175-2023";
        final String aerRequestYearBeforeId = "AEM00175-2022";
        final String aerRequestTwoYearsBeforeId = "AEM00175-2021";
        final String annualOffsettingRequestId = "AEM-AO-00175-3";
        final String annualOffsettingRequestYearBeforeId = "AEM-AO-00175-2";
        final String annualOffsettingRequestTwoYearsBeforeId = "AEM-AO-00175-1";
        Year year1 = Year.of(2021);
        Year year2 = Year.of(2022);
        Year year3 = Year.of(2023);
        HashMap<String, Boolean> sectionsCompleted = new HashMap<>();
        sectionsCompleted.put("Test", true);
        DecisionNotification decisionNotification = DecisionNotification.builder().signatory("signatory").build();

        AviationAerCorsia3YearPeriodOffsettingRequestMetadata metadata =
                AviationAerCorsia3YearPeriodOffsettingRequestMetadata
                        .builder()
                        .year(year3)
                        .years(List.of(year1, year2, year3))
                        .currentAerId(aerRequestId)
                        .build();

        AviationAerCorsia3YearPeriodOffsettingRequestPayload payload = AviationAerCorsia3YearPeriodOffsettingRequestPayload.builder()
                .aviationAerCorsia3YearPeriodOffsettingSectionsCompleted(sectionsCompleted)
                .decisionNotification(decisionNotification)
                .build();

        Request request = Request.builder().payload(payload).metadata(metadata).build();

        final Request aerRequest = Request.builder()
                                .id(aerRequestId)
                                .accountId(accountId)
                                .type(RequestType.AVIATION_AER_CORSIA)
                                .payload(AviationAerCorsiaRequestPayload
                                        .builder()
                                        .submittedEmissions(AviationAerCorsiaSubmittedEmissions
                                                .builder()
                                                .totalEmissions(AviationAerCorsiaTotalEmissions
                                                        .builder()
                                                        .emissionsReductionClaim(BigDecimal.valueOf(100))
                                                        .build())
                                                .build())
                                        .build())
                                .status(RequestStatus.COMPLETED)
                                .metadata(AviationAerCorsiaRequestMetadata.builder().year(year3).build())
                        .build();

        final Request aerRequestYearBefore = Request.builder()
                                .id(aerRequestYearBeforeId)
                                .accountId(accountId)
                                .type(RequestType.AVIATION_AER_CORSIA)
                                .payload(AviationAerCorsiaRequestPayload
                                        .builder()
                                        .submittedEmissions(AviationAerCorsiaSubmittedEmissions
                                                .builder()
                                                .totalEmissions(AviationAerCorsiaTotalEmissions
                                                        .builder()
                                                        .emissionsReductionClaim(BigDecimal.valueOf(200))
                                                        .build())
                                                .build())
                                        .build())
                                .status(RequestStatus.COMPLETED)
                                .metadata(AviationAerCorsiaRequestMetadata.builder().year(year2).build())
                        .build();

        final Request aerRequestTwoYearsBeforeBefore = Request.builder()
                                .id(aerRequestTwoYearsBeforeId)
                                .accountId(accountId)
                                .type(RequestType.AVIATION_AER_CORSIA)
                                .payload(AviationAerCorsiaRequestPayload
                                        .builder()
                                        .submittedEmissions(AviationAerCorsiaSubmittedEmissions
                                                .builder()
                                                .totalEmissions(AviationAerCorsiaTotalEmissions
                                                        .builder()
                                                        .emissionsReductionClaim(BigDecimal.valueOf(300))
                                                        .build())
                                                .build())
                                        .build())
                                .status(RequestStatus.COMPLETED)
                                .metadata(AviationAerCorsiaRequestMetadata.builder().year(year1).build())
                        .build();

        final Request annualOffsetting = Request.builder()
                                .id(annualOffsettingRequestId)
                                .accountId(accountId)
                                .type(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING)
                                .payload(AviationAerCorsiaAnnualOffsettingRequestPayload
                                        .builder()
                                        .aviationAerCorsiaAnnualOffsetting(AviationAerCorsiaAnnualOffsetting
                                                .builder()
                                                .calculatedAnnualOffsetting(1000)
                                                .build())
                                        .build())
                                .status(RequestStatus.COMPLETED)
                                .metadata(AviationAerCorsiaAnnualOffsettingRequestMetadata.builder().year(year3).build())
                        .build();

        final Request annualOffsettingYearBefore = Request.builder()
                                .id(annualOffsettingRequestYearBeforeId)
                                .accountId(accountId)
                                .type(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING)
                                .payload(AviationAerCorsiaAnnualOffsettingRequestPayload
                                        .builder()
                                        .aviationAerCorsiaAnnualOffsetting(AviationAerCorsiaAnnualOffsetting
                                                .builder()
                                                .calculatedAnnualOffsetting(2000)
                                                .build())
                                        .build())
                                .status(RequestStatus.COMPLETED)
                                .metadata(AviationAerCorsiaAnnualOffsettingRequestMetadata.builder().year(year2).build())
                        .build();

        final Request annualOffsettingTwoYearsBefore = Request.builder()
                                .id(annualOffsettingRequestTwoYearsBeforeId)
                                .accountId(accountId)
                                .type(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING)
                                .payload(AviationAerCorsiaAnnualOffsettingRequestPayload
                                        .builder()
                                        .aviationAerCorsiaAnnualOffsetting(AviationAerCorsiaAnnualOffsetting
                                                .builder()
                                                .calculatedAnnualOffsetting(3000)
                                                .build())
                                        .build())
                                .status(RequestStatus.COMPLETED)
                                .metadata(AviationAerCorsiaAnnualOffsettingRequestMetadata.builder().year(year1).build())
                        .build();


        Map<Year, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> expectedYearlyOffsettingData = Map.of(
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
        
        when(aviationAerRequestIdGenerator
                .generatePastAerId(aerRequest.getAccountId(), year3,1)).thenReturn(aerRequestYearBeforeId);
        when(aviationAerRequestIdGenerator
                .generatePastAerId(aerRequest.getAccountId(), year3,2)).thenReturn(aerRequestTwoYearsBeforeId);
        when(requestService.findRequestById(aerRequestId)).thenReturn(aerRequest);
        when(aviationAerRequestQueryService.findAviationAerById(aerRequestYearBeforeId)).thenReturn(Optional.of(aerRequestYearBefore));
        when(aviationAerRequestQueryService.findAviationAerById(aerRequestTwoYearsBeforeId)).thenReturn(Optional.of(aerRequestTwoYearsBeforeBefore));
        when(annualOffsettingRequestQueryService.findLatestCorsiaAnnualOffsettingRequestForYear(accountId,year3)).thenReturn(Optional.of(annualOffsetting));
        when(annualOffsettingRequestQueryService.findLatestCorsiaAnnualOffsettingRequestForYear(accountId,year2)).thenReturn(Optional.of(annualOffsettingYearBefore));
        when(annualOffsettingRequestQueryService.findLatestCorsiaAnnualOffsettingRequestForYear(accountId,year1)).thenReturn(Optional.of(annualOffsettingTwoYearsBefore));


        AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload result =
                (AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);


        assertNotNull(result);
        assertEquals(RequestTaskPayloadType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT_PAYLOAD, result.getPayloadType());
        assertThat(result.getAviationAerCorsia3YearPeriodOffsettingSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(result.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(result.getAviationAerCorsia3YearPeriodOffsetting().getSchemeYears()).containsExactly(year1, year2, year3);
        assertThat(result.getAviationAerCorsia3YearPeriodOffsetting().getYearlyOffsettingData())
                .containsExactlyEntriesOf(expectedYearlyOffsettingData);
        assertThat(result.getAviationAerCorsia3YearPeriodOffsetting().getTotalYearlyOffsettingData()).isEqualTo(AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                .builder()
                .calculatedAnnualOffsetting(6000L)
                .cefEmissionsReductions(600L)
                .build());

        assertThat(result.getAviationAerCorsia3YearPeriodOffsetting().getPeriodOffsettingRequirements()).isEqualTo(5400);
        assertThat(result.getAviationAerCorsia3YearPeriodOffsetting().getPeriodOffsettingRequirements()).isEqualTo(5400);

        verify(aviationAerRequestIdGenerator, times(1)).generatePastAerId(accountId, year3, 1);
        verify(aviationAerRequestIdGenerator, times(1)).generatePastAerId(accountId, year3, 2);
        verify(requestService, times(1)).findRequestById(aerRequestId);
        verify(aviationAerRequestQueryService, times(1)).findAviationAerById(aerRequestYearBeforeId);
        verify(aviationAerRequestQueryService, times(1)).findAviationAerById(aerRequestTwoYearsBeforeId);
        verify(annualOffsettingRequestQueryService, times(1))
                .findLatestCorsiaAnnualOffsettingRequestForYear(accountId, year1);
        verify(annualOffsettingRequestQueryService, times(1))
                .findLatestCorsiaAnnualOffsettingRequestForYear(accountId, year2);
        verify(annualOffsettingRequestQueryService, times(1))
                .findLatestCorsiaAnnualOffsettingRequestForYear(accountId, year3);

        verify(calculationsService, times(1))
                .calculateTotalCalculatedAnnualOffsettingFromYearlyOffsettingData(expectedYearlyOffsettingData);

        verify(calculationsService, times(1))
                .calculateTotalCefEmissionsReductionsFromYearlyOffsettingData(expectedYearlyOffsettingData);

        verify(calculationsService, times(1))
                .calculatePeriodOffsettingRequirements(6000L,600L);
    }

    @Test
    void initializePayload_threeYearPeriodOffsettingAlreadyExistsInRequestPayload_returnTheExistingData() {

        Year year1 = Year.of(2021);
        Year year2 = Year.of(2022);
        Year year3 = Year.of(2023);
        HashMap<String, Boolean> sectionsCompleted = new HashMap<>();
        sectionsCompleted.put("Test", true);
        DecisionNotification decisionNotification = DecisionNotification.builder().signatory("signatory").build();

        AviationAerCorsia3YearPeriodOffsettingRequestMetadata metadata =
                AviationAerCorsia3YearPeriodOffsettingRequestMetadata
                        .builder()
                        .year(year3)
                        .years(List.of(year1, year2, year3))
                        .currentAerId("aerRequestId")
                        .build();


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

        AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData totalYearlyOffsettingData =
                AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                        .builder()
                        .calculatedAnnualOffsetting(6000L)
                        .cefEmissionsReductions(600L)
                        .build();

        AviationAerCorsia3YearPeriodOffsetting aviationAerCorsia3YearPeriodOffsetting =
                AviationAerCorsia3YearPeriodOffsetting
                    .builder()
                        .schemeYears(List.of(year1, year2, year3))
                        .operatorHaveOffsettingRequirements(true)
                        .yearlyOffsettingData(yearlyOffsettingData)
                        .totalYearlyOffsettingData(totalYearlyOffsettingData)
                        .periodOffsettingRequirements(5400L)
                    .build();


        AviationAerCorsia3YearPeriodOffsettingRequestPayload payload = AviationAerCorsia3YearPeriodOffsettingRequestPayload.builder()
                .aviationAerCorsia3YearPeriodOffsettingSectionsCompleted(sectionsCompleted)
                .decisionNotification(decisionNotification)
                .aviationAerCorsia3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting)
                .build();

        Request request = Request.builder().payload(payload).metadata(metadata).build();

        AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload result =
                (AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);


        assertNotNull(result);
        assertEquals(RequestTaskPayloadType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT_PAYLOAD, result.getPayloadType());
        assertThat(result.getAviationAerCorsia3YearPeriodOffsettingSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(result.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(result.getAviationAerCorsia3YearPeriodOffsetting()).isEqualTo(aviationAerCorsia3YearPeriodOffsetting);

    }

    @Test
    void initializePayload_annualOffsettingDoesntExist_itsCalculatedAnnualOffsettingIsEmpty() {

        final Long accountId = 175L;
        final String aerRequestId = "AEM00175-2023";
        final String aerRequestYearBeforeId = "AEM00175-2022";
        final String aerRequestTwoYearsBeforeId = "AEM00175-2021";
        final String annualOffsettingRequestId = "AEM-AO-00175-3";
        final String annualOffsettingRequestYearBeforeId = "AEM-AO-00175-2";

        Year year1 = Year.of(2021);
        Year year2 = Year.of(2022);
        Year year3 = Year.of(2023);
        HashMap<String, Boolean> sectionsCompleted = new HashMap<>();
        sectionsCompleted.put("Test", true);
        DecisionNotification decisionNotification = DecisionNotification.builder().signatory("signatory").build();

        AviationAerCorsia3YearPeriodOffsettingRequestMetadata metadata =
                AviationAerCorsia3YearPeriodOffsettingRequestMetadata
                        .builder()
                        .year(year3)
                        .years(List.of(year1, year2, year3))
                        .currentAerId(aerRequestId)
                        .build();

        AviationAerCorsia3YearPeriodOffsettingRequestPayload payload = AviationAerCorsia3YearPeriodOffsettingRequestPayload.builder()
                .aviationAerCorsia3YearPeriodOffsettingSectionsCompleted(sectionsCompleted)
                .decisionNotification(decisionNotification)
                .build();

        Request request = Request.builder().payload(payload).metadata(metadata).build();

        final Request aerRequest = Request.builder()
                                .id(aerRequestId)
                                .accountId(accountId)
                                .type(RequestType.AVIATION_AER_CORSIA)
                                 .payload(AviationAerCorsiaRequestPayload
                                        .builder()
                                        .submittedEmissions(AviationAerCorsiaSubmittedEmissions
                                                .builder()
                                                .totalEmissions(AviationAerCorsiaTotalEmissions
                                                        .builder()
                                                        .emissionsReductionClaim(BigDecimal.valueOf(100))
                                                        .build())
                                                .build())
                                        .build())
                                .status(RequestStatus.COMPLETED)
                                .metadata(AviationAerCorsiaRequestMetadata.builder().year(year3).build())
                        .build();

        final Request aerRequestYearBefore = Request.builder()
                                .id(aerRequestYearBeforeId)
                                .accountId(accountId)
                                .type(RequestType.AVIATION_AER_CORSIA)
                                .payload(AviationAerCorsiaRequestPayload
                                        .builder()
                                        .submittedEmissions(AviationAerCorsiaSubmittedEmissions
                                                .builder()
                                                .totalEmissions(AviationAerCorsiaTotalEmissions
                                                        .builder()
                                                        .emissionsReductionClaim(BigDecimal.valueOf(200))
                                                        .build())
                                                .build())
                                        .build())
                                .status(RequestStatus.COMPLETED)
                                .metadata(AviationAerCorsiaRequestMetadata.builder().year(year2).build())
                        .build();

        final Request aerRequestTwoYearsBeforeBefore = Request.builder()
                                .id(aerRequestTwoYearsBeforeId)
                                .accountId(accountId)
                                .type(RequestType.AVIATION_AER_CORSIA)
                                .payload(AviationAerCorsiaRequestPayload
                                        .builder()
                                        .submittedEmissions(AviationAerCorsiaSubmittedEmissions
                                                .builder()
                                                .totalEmissions(AviationAerCorsiaTotalEmissions
                                                        .builder()
                                                        .emissionsReductionClaim(BigDecimal.valueOf(300))
                                                        .build())
                                                .build())
                                        .build())
                                .status(RequestStatus.COMPLETED)
                                .metadata(AviationAerCorsiaRequestMetadata.builder().year(year1).build())
                        .build();

        final Request annualOffsetting = Request.builder()
                                .id(annualOffsettingRequestId)
                                .accountId(accountId)
                                .type(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING)
                                .payload(AviationAerCorsiaAnnualOffsettingRequestPayload
                                        .builder()
                                        .aviationAerCorsiaAnnualOffsetting(AviationAerCorsiaAnnualOffsetting
                                                .builder()
                                                .calculatedAnnualOffsetting(1000)
                                                .build())
                                        .build())
                                .status(RequestStatus.COMPLETED)
                                .metadata(AviationAerCorsiaAnnualOffsettingRequestMetadata.builder().year(year3).build())
                        .build();

        final Request annualOffsettingYearBefore = Request.builder()
                                .id(annualOffsettingRequestYearBeforeId)
                                .accountId(accountId)
                                .type(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING)
                                .payload(AviationAerCorsiaAnnualOffsettingRequestPayload
                                        .builder()
                                        .aviationAerCorsiaAnnualOffsetting(AviationAerCorsiaAnnualOffsetting
                                                .builder()
                                                .calculatedAnnualOffsetting(2000)
                                                .build())
                                        .build())
                                .status(RequestStatus.COMPLETED)
                                .metadata(AviationAerCorsiaAnnualOffsettingRequestMetadata.builder().year(year2).build())
                        .build();

        Map<Year, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> expectedYearlyOffsettingData = Map.of(
                        year1, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
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

        when(aviationAerRequestIdGenerator
                .generatePastAerId(aerRequest.getAccountId(), year3,1)).thenReturn(aerRequestYearBeforeId);
        when(aviationAerRequestIdGenerator
                .generatePastAerId(aerRequest.getAccountId(), year3,2)).thenReturn(aerRequestTwoYearsBeforeId);
        when(requestService.findRequestById(aerRequestId)).thenReturn(aerRequest);
        when(aviationAerRequestQueryService.findAviationAerById(aerRequestYearBeforeId)).thenReturn(Optional.of(aerRequestYearBefore));
        when(aviationAerRequestQueryService.findAviationAerById(aerRequestTwoYearsBeforeId)).thenReturn(Optional.of(aerRequestTwoYearsBeforeBefore));
        when(annualOffsettingRequestQueryService.findLatestCorsiaAnnualOffsettingRequestForYear(accountId,year3)).thenReturn(Optional.of(annualOffsetting));
        when(annualOffsettingRequestQueryService.findLatestCorsiaAnnualOffsettingRequestForYear(accountId,year2)).thenReturn(Optional.of(annualOffsettingYearBefore));
        when(annualOffsettingRequestQueryService.findLatestCorsiaAnnualOffsettingRequestForYear(accountId,year1)).thenReturn(Optional.empty());


        AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload result =
                (AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);


        assertNotNull(result);
        assertEquals(RequestTaskPayloadType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT_PAYLOAD, result.getPayloadType());
        assertThat(result.getAviationAerCorsia3YearPeriodOffsettingSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(result.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(result.getAviationAerCorsia3YearPeriodOffsetting().getSchemeYears()).containsExactly(year1, year2, year3);
        assertThat(result.getAviationAerCorsia3YearPeriodOffsetting().getYearlyOffsettingData())
                .containsExactlyEntriesOf(expectedYearlyOffsettingData);
        assertThat(result.getAviationAerCorsia3YearPeriodOffsetting().getTotalYearlyOffsettingData()).isEqualTo(AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                .builder()
                .calculatedAnnualOffsetting(3000L)
                .cefEmissionsReductions(600L)
                .build());

        assertThat(result.getAviationAerCorsia3YearPeriodOffsetting().getPeriodOffsettingRequirements()).isEqualTo(2400L);

        verify(aviationAerRequestIdGenerator, times(1)).generatePastAerId(accountId, year3, 1);
        verify(aviationAerRequestIdGenerator, times(1)).generatePastAerId(accountId, year3, 2);
        verify(requestService, times(1)).findRequestById(aerRequestId);
        verify(aviationAerRequestQueryService, times(1)).findAviationAerById(aerRequestYearBeforeId);
        verify(aviationAerRequestQueryService, times(1)).findAviationAerById(aerRequestTwoYearsBeforeId);
        verify(annualOffsettingRequestQueryService, times(1))
                .findLatestCorsiaAnnualOffsettingRequestForYear(accountId, year1);
        verify(annualOffsettingRequestQueryService, times(1))
                .findLatestCorsiaAnnualOffsettingRequestForYear(accountId, year2);
        verify(annualOffsettingRequestQueryService, times(1))
                .findLatestCorsiaAnnualOffsettingRequestForYear(accountId, year3);

        verify(calculationsService, times(1))
                .calculateTotalCalculatedAnnualOffsettingFromYearlyOffsettingData(expectedYearlyOffsettingData);

        verify(calculationsService, times(1))
                .calculateTotalCefEmissionsReductionsFromYearlyOffsettingData(expectedYearlyOffsettingData);

        verify(calculationsService, times(1))
                .calculatePeriodOffsettingRequirements(3000L,600L);
    }

    @Test
    void initializePayload_aerDoesntExist_itsCefEmissionsReductionsIsEmpty() {

        final Long accountId = 175L;
        final String aerRequestId = "AEM00175-2023";
        final String aerRequestYearBeforeId = "AEM00175-2022";
        final String aerRequestTwoYearsBeforeId = "AEM00175-2021";
        final String annualOffsettingRequestId = "AEM-AO-00175-3";
        final String annualOffsettingRequestYearBeforeId = "AEM-AO-00175-2";
        final String annualOffsettingRequestTwoYearsBeforeId = "AEM-AO-00175-1";
        
        Year year1 = Year.of(2021);
        Year year2 = Year.of(2022);
        Year year3 = Year.of(2023);
        HashMap<String, Boolean> sectionsCompleted = new HashMap<>();
        sectionsCompleted.put("Test", true);
        DecisionNotification decisionNotification = DecisionNotification.builder().signatory("signatory").build();

        AviationAerCorsia3YearPeriodOffsettingRequestMetadata metadata =
                AviationAerCorsia3YearPeriodOffsettingRequestMetadata
                        .builder()
                        .year(year3)
                        .years(List.of(year1, year2, year3))
                        .currentAerId(aerRequestId)
                        .build();

        AviationAerCorsia3YearPeriodOffsettingRequestPayload payload = AviationAerCorsia3YearPeriodOffsettingRequestPayload.builder()
                .aviationAerCorsia3YearPeriodOffsettingSectionsCompleted(sectionsCompleted)
                .decisionNotification(decisionNotification)
                .build();

        Request request = Request.builder().payload(payload).metadata(metadata).build();

        final Request aerRequest = Request.builder()
                                .id(aerRequestId)
                                .accountId(accountId)
                                .type(RequestType.AVIATION_AER_CORSIA)
                                .payload(AviationAerCorsiaRequestPayload
                                        .builder()
                                        .submittedEmissions(AviationAerCorsiaSubmittedEmissions
                                                .builder()
                                                .totalEmissions(AviationAerCorsiaTotalEmissions
                                                        .builder()
                                                        .emissionsReductionClaim(BigDecimal.valueOf(100))
                                                        .build())
                                                .build())
                                        .build())
                                .status(RequestStatus.COMPLETED)
                                .metadata(AviationAerCorsiaRequestMetadata.builder().year(year3).build())
                        .build();


        final Request aerRequestTwoYearsBeforeBefore = Request.builder()
                                .id(aerRequestTwoYearsBeforeId)
                                .accountId(accountId)
                                .type(RequestType.AVIATION_AER_CORSIA)
                                .payload(AviationAerCorsiaRequestPayload
                                        .builder()
                                        .submittedEmissions(AviationAerCorsiaSubmittedEmissions
                                                .builder()
                                                .totalEmissions(AviationAerCorsiaTotalEmissions
                                                        .builder()
                                                        .emissionsReductionClaim(BigDecimal.valueOf(300))
                                                        .build())
                                                .build())
                                        .build())
                                .status(RequestStatus.COMPLETED)
                                .metadata(AviationAerCorsiaRequestMetadata.builder().year(year1).build())
                        .build();

        final Request annualOffsetting = Request.builder()
                                .id(annualOffsettingRequestId)
                                .accountId(accountId)
                                .type(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING)
                                .payload(AviationAerCorsiaAnnualOffsettingRequestPayload
                                        .builder()
                                        .aviationAerCorsiaAnnualOffsetting(AviationAerCorsiaAnnualOffsetting
                                                .builder()
                                                .calculatedAnnualOffsetting(1000)
                                                .build())
                                        .build())
                                .status(RequestStatus.COMPLETED)
                                .metadata(AviationAerCorsiaAnnualOffsettingRequestMetadata.builder().year(year3).build())
                        .build();

        final Request annualOffsettingYearBefore = Request.builder()
                                .id(annualOffsettingRequestYearBeforeId)
                                .accountId(accountId)
                                .type(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING)
                                .payload(AviationAerCorsiaAnnualOffsettingRequestPayload
                                        .builder()
                                        .aviationAerCorsiaAnnualOffsetting(AviationAerCorsiaAnnualOffsetting
                                                .builder()
                                                .calculatedAnnualOffsetting(2000)
                                                .build())
                                        .build())
                                .status(RequestStatus.COMPLETED)
                                .metadata(AviationAerCorsiaAnnualOffsettingRequestMetadata.builder().year(year2).build())
                        .build();

        final Request annualOffsettingTwoYearsBefore = Request.builder()
                                .id(annualOffsettingRequestTwoYearsBeforeId)
                                .accountId(accountId)
                                .type(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING)
                                .payload(AviationAerCorsiaAnnualOffsettingRequestPayload
                                        .builder()
                                        .aviationAerCorsiaAnnualOffsetting(AviationAerCorsiaAnnualOffsetting
                                                .builder()
                                                .calculatedAnnualOffsetting(3000)
                                                .build())
                                        .build())
                                .status(RequestStatus.COMPLETED)
                                .metadata(AviationAerCorsiaAnnualOffsettingRequestMetadata.builder().year(year1).build())
                        .build();

        Map<Year, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> expectedYearlyOffsettingData = Map.of(
                        year1, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(3000L)
                                .cefEmissionsReductions(300L)
                                .build(),
                        year2, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(2000L)
                                .build(),
                        year3, AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(1000L)
                                .cefEmissionsReductions(100L)
                                .build()
                );


        when(aviationAerRequestIdGenerator
                .generatePastAerId(aerRequest.getAccountId(), year3,1)).thenReturn(aerRequestYearBeforeId);
        when(aviationAerRequestIdGenerator
                .generatePastAerId(aerRequest.getAccountId(), year3,2)).thenReturn(aerRequestTwoYearsBeforeId);
        when(requestService.findRequestById(aerRequestId)).thenReturn(aerRequest);
        when(aviationAerRequestQueryService.findAviationAerById(aerRequestYearBeforeId)).thenReturn(Optional.empty());
        when(aviationAerRequestQueryService.findAviationAerById(aerRequestTwoYearsBeforeId)).thenReturn(Optional.of(aerRequestTwoYearsBeforeBefore));
        when(annualOffsettingRequestQueryService.findLatestCorsiaAnnualOffsettingRequestForYear(accountId,year3)).thenReturn(Optional.of(annualOffsetting));
        when(annualOffsettingRequestQueryService.findLatestCorsiaAnnualOffsettingRequestForYear(accountId,year2)).thenReturn(Optional.of(annualOffsettingYearBefore));
        when(annualOffsettingRequestQueryService.findLatestCorsiaAnnualOffsettingRequestForYear(accountId,year1)).thenReturn(Optional.of(annualOffsettingTwoYearsBefore));


        AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload result =
                (AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);


        assertNotNull(result);
        assertEquals(RequestTaskPayloadType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT_PAYLOAD, result.getPayloadType());
        assertThat(result.getAviationAerCorsia3YearPeriodOffsettingSectionsCompleted()).isEqualTo(sectionsCompleted);
        assertThat(result.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(result.getAviationAerCorsia3YearPeriodOffsetting().getSchemeYears()).containsExactly(year1, year2, year3);
        assertThat(result.getAviationAerCorsia3YearPeriodOffsetting().getYearlyOffsettingData())
                .containsExactlyEntriesOf(expectedYearlyOffsettingData);
        assertThat(result.getAviationAerCorsia3YearPeriodOffsetting().getTotalYearlyOffsettingData()).isEqualTo(AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                .builder()
                .cefEmissionsReductions(400L)
                .calculatedAnnualOffsetting(6000L)
                .build());

        verify(aviationAerRequestIdGenerator, times(1)).generatePastAerId(accountId, year3, 1);
        verify(aviationAerRequestIdGenerator, times(1)).generatePastAerId(accountId, year3, 2);
        verify(requestService, times(1)).findRequestById(aerRequestId);
        verify(aviationAerRequestQueryService, times(1)).findAviationAerById(aerRequestTwoYearsBeforeId);
        verify(annualOffsettingRequestQueryService, times(1))
                .findLatestCorsiaAnnualOffsettingRequestForYear(accountId, year1);
        verify(annualOffsettingRequestQueryService, times(1))
                .findLatestCorsiaAnnualOffsettingRequestForYear(accountId, year2);
        verify(annualOffsettingRequestQueryService, times(1))
                .findLatestCorsiaAnnualOffsettingRequestForYear(accountId, year3);

        verify(calculationsService, times(1))
                .calculateTotalCalculatedAnnualOffsettingFromYearlyOffsettingData(expectedYearlyOffsettingData);

        verify(calculationsService, times(1))
                .calculateTotalCefEmissionsReductionsFromYearlyOffsettingData(expectedYearlyOffsettingData);

        verify(calculationsService, times(1))
                .calculatePeriodOffsettingRequirements(6000L,400L);
    }

    @Test
    void testGetRequestTaskTypes() {
        // Act
        Set<RequestTaskType> result = initializer.getRequestTaskTypes();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(RequestTaskType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT));
    }
}
