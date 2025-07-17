package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.service;

import java.time.Year;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsetting;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.validation.AviationAerCorsia3YearPeriodOffsettingValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AviationAerCorsia3YearPeriodOffsettingSubmitServiceTest {

    @InjectMocks
    private AviationAerCorsia3YearPeriodOffsettingSubmitService service;

    @Mock
    private RequestService requestService;

    @Mock
    private AviationAerCorsia3YearPeriodOffsettingValidator validator;

    @Mock
    private DecisionNotificationUsersValidator usersValidator;

    @Test
    void applySaveAction() {

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
                .periodOffsettingRequirements(5400L)
                .operatorHaveOffsettingRequirements(false)
                .build();

        AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload taskPayload =
                AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload
                        .builder()
                        .build();

        AviationAerCorsia3YearPeriodOffsettingRequestPayload requestPayload =
                AviationAerCorsia3YearPeriodOffsettingRequestPayload
                        .builder()
                        .build();


        Request request = Request
                .builder()
                .payload(requestPayload)
                .metadata(AviationAerCorsia3YearPeriodOffsettingRequestMetadata.builder().build())
                .build();

        RequestTask requestTask = RequestTask
                .builder()
                .type(RequestTaskType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT)
                .payload(taskPayload)
                .request(request)
                .build();

        AviationAerCorsia3YearPeriodOffsettingSaveRequestTaskActionPayload taskActionPayload =
                AviationAerCorsia3YearPeriodOffsettingSaveRequestTaskActionPayload
                        .builder()
                        .aviationAerCorsia3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting)
                        .aviationAerCorsia3YearPeriodOffsettingSectionsCompleted(Map.of("test",true))
                        .build();

        service.applySaveAction(requestTask,taskActionPayload);

        assertThat(taskPayload.getAviationAerCorsia3YearPeriodOffsetting()).isEqualTo(taskActionPayload.getAviationAerCorsia3YearPeriodOffsetting());
        assertThat(taskPayload.getAviationAerCorsia3YearPeriodOffsettingSectionsCompleted()).isEqualTo(taskActionPayload.getAviationAerCorsia3YearPeriodOffsettingSectionsCompleted());
    }

    @Test
    void requestPeerReview(){

        AppUser appUser = AppUser.builder().userId("user").build();
        String peerReviewer = "peerReviewer";

        Map<String, Boolean> aviationAerCorsia3YearPeriodOffsettingSectionsCompleted  = Map.of("test",true);

        AviationAerCorsia3YearPeriodOffsetting aviationAerCorsia3YearPeriodOffsetting =
                AviationAerCorsia3YearPeriodOffsetting
                        .builder()
                        .schemeYears(List.of(Year.of(2021),Year.of(2022),Year.of(2023)))
                        .yearlyOffsettingData(Map.of(
                                Year.of(2021),
                                AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                        .builder()
                                        .calculatedAnnualOffsetting(3000L)
                                        .cefEmissionsReductions(300L)
                                        .build(),

                                Year.of(2022),
                                AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                        .builder()
                                        .calculatedAnnualOffsetting(2000L)
                                        .cefEmissionsReductions(200L)
                                        .build(),

                                Year.of(2023),
                                AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                        .builder()
                                        .calculatedAnnualOffsetting(1000L)
                                        .cefEmissionsReductions(100L)
                                        .build()
                        ))
                        .totalYearlyOffsettingData(
                                AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                        .builder()
                                        .calculatedAnnualOffsetting(6000L)
                                        .cefEmissionsReductions(600L)
                                        .build()
                        )
                        .periodOffsettingRequirements(5400L)
                        .operatorHaveOffsettingRequirements(false)
                        .build();

        AviationAerCorsia3YearPeriodOffsettingRequestPayload requestPayload =
                AviationAerCorsia3YearPeriodOffsettingRequestPayload.builder().build();

        Request request = Request
                .builder()
                .payload(requestPayload)
                .build();

        AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload requestTaskPayload =
                AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload
                        .builder()
                        .aviationAerCorsia3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting)
                        .aviationAerCorsia3YearPeriodOffsettingSectionsCompleted(aviationAerCorsia3YearPeriodOffsettingSectionsCompleted)
                        .build();

        RequestTask requestTask = RequestTask
                .builder()
                .payload(requestTaskPayload)
                .request(request)
                .build();

        service.requestPeerReview(requestTask, peerReviewer, appUser);


        AviationAerCorsia3YearPeriodOffsettingRequestPayload updatedRequestPayload =
                (AviationAerCorsia3YearPeriodOffsettingRequestPayload) requestTask.getRequest().getPayload();

        assertThat(updatedRequestPayload.getRegulatorPeerReviewer()).isEqualTo(peerReviewer);
        assertThat(updatedRequestPayload.getRegulatorReviewer()).isEqualTo(appUser.getUserId());
        assertThat(updatedRequestPayload.getAviationAerCorsia3YearPeriodOffsettingSectionsCompleted()).containsExactlyEntriesOf(aviationAerCorsia3YearPeriodOffsettingSectionsCompleted);
        assertThat(updatedRequestPayload.getAviationAerCorsia3YearPeriodOffsetting()).isEqualTo(aviationAerCorsia3YearPeriodOffsetting);
    }

    @Test
    void testApplySubmitNotify_aviationAerCorsia3yearOffsettingIsValid_successfullyNotifyOperator() {

        AppUser appUser = AppUser.builder().userId("user").build();
        AviationAerCorsia3YearPeriodOffsetting aviationAerCorsia3YearPeriodOffsetting =
                AviationAerCorsia3YearPeriodOffsetting.builder().operatorHaveOffsettingRequirements(false).build();

        DecisionNotification decisionNotification = DecisionNotification.builder().build();

        AviationAerCorsia3YearPeriodOffsettingRequestPayload requestPayload =
                AviationAerCorsia3YearPeriodOffsettingRequestPayload
                        .builder()
                        .build();


        AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload requestTaskPayload =
                AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload
                        .builder()
                        .aviationAerCorsia3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting)
                        .aviationAerCorsia3YearPeriodOffsettingSectionsCompleted(Map.of("test",true))
                        .build();

        AviationAerCorsia3YearPeriodOffsettingRequestMetadata requestMetadata =
                AviationAerCorsia3YearPeriodOffsettingRequestMetadata.builder().build();

        RequestTask requestTask = RequestTask
                .builder()
                .payload(requestTaskPayload)
                .request(Request
                        .builder()
                        .payload(requestPayload)
                        .metadata(requestMetadata)
                        .build())
                .build();


        when(usersValidator.areUsersValid(requestTask, decisionNotification, appUser)).thenReturn(true);

        service.applySubmitNotify(requestTask, decisionNotification, appUser);

        verify(validator, times(1)).validate3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting);
        assertThat(requestPayload.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(requestPayload.getAviationAerCorsia3YearPeriodOffsetting()).isEqualTo(requestTaskPayload.getAviationAerCorsia3YearPeriodOffsetting());
        assertThat(requestPayload.getAviationAerCorsia3YearPeriodOffsettingSectionsCompleted()).containsExactlyEntriesOf(requestTaskPayload.getAviationAerCorsia3YearPeriodOffsettingSectionsCompleted());
        assertThat(requestMetadata.getPeriodOffsettingRequirements()).isEqualTo(requestTaskPayload.getAviationAerCorsia3YearPeriodOffsetting().getPeriodOffsettingRequirements());
        assertThat(requestMetadata.getOperatorHaveOffsettingRequirements()).isEqualTo(requestTaskPayload.getAviationAerCorsia3YearPeriodOffsetting().getOperatorHaveOffsettingRequirements());
    }

    @Test
    void testApplySubmitNotify_aviationAerCorsia3yearOffsettingIsInvalid_throwFormValidationFailed() {

        AppUser appUser = AppUser.builder().userId("user").build();

        AviationAerCorsia3YearPeriodOffsetting aviationAerCorsia3YearPeriodOffsetting =
                AviationAerCorsia3YearPeriodOffsetting
                        .builder()
                        .yearlyOffsettingData(Map.of(
                                Year.of(2021),
                                AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                        .builder()
                                        .cefEmissionsReductions(1L)
                                        .calculatedAnnualOffsetting(10L)
                                        .build(),
                                Year.of(2022),
                                AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                        .builder()
                                        .cefEmissionsReductions(2L)
                                        .calculatedAnnualOffsetting(20L)
                                        .build(),
                                Year.of(2023),
                                AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                        .builder()
                                        .cefEmissionsReductions(3L)
                                        .calculatedAnnualOffsetting(30L)
                                        .build()
                        ))
                        .totalYearlyOffsettingData(AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData
                                .builder()
                                .calculatedAnnualOffsetting(50L)
                                .cefEmissionsReductions(6L)
                                .build())
                        .build();

        DecisionNotification decisionNotification = DecisionNotification.builder().build();

        AviationAerCorsia3YearPeriodOffsettingRequestPayload requestPayload =
                AviationAerCorsia3YearPeriodOffsettingRequestPayload
                        .builder()
                        .build();


        AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload requestTaskPayload =
                AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload
                        .builder()
                        .aviationAerCorsia3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting)
                        .aviationAerCorsia3YearPeriodOffsettingSectionsCompleted(Map.of("test",true))
                        .build();

        AviationAerCorsia3YearPeriodOffsettingRequestMetadata requestMetadata =
                AviationAerCorsia3YearPeriodOffsettingRequestMetadata.builder().build();

        RequestTask requestTask = RequestTask
                .builder()
                .payload(requestTaskPayload)
                .request(Request
                        .builder()
                        .payload(requestPayload)
                        .metadata(requestMetadata)
                        .build())
                .build();


        doThrow(new BusinessException(ErrorCode.FORM_VALIDATION)).when(validator).validate3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                service.applySubmitNotify(requestTask, decisionNotification, appUser));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
        verify(validator, times(1)).validate3YearPeriodOffsetting(aviationAerCorsia3YearPeriodOffsetting);
    }


    @Test
    void testCancel() {
        Request request1 = Request.builder()
                .payload(AviationAerCorsia3YearPeriodOffsettingRequestPayload.builder()
                        .regulatorAssignee("user1").build()).build();

        String requestId = "requestId";
        when(requestService.findRequestById(requestId)).thenReturn(request1);
        service.cancel(requestId);
        verify(requestService).addActionToRequest(request1, null,
                RequestActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_CANCELLED,
                request1.getPayload().getRegulatorAssignee());
    }

    @Test
    void testGetRequestType() {
        RequestType result = service.getRequestType();
        Assertions.assertEquals(RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING, result);
    }


}
