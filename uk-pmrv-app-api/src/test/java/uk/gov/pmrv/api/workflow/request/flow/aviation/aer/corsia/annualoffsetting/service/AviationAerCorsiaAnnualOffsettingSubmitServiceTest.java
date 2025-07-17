package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.*;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.service.AviationAerCorsiaAnnualOffsettingValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

class AviationAerCorsiaAnnualOffsettingSubmitServiceTest {

    @InjectMocks
    private AviationAerCorsiaAnnualOffsettingSubmitService service;

    @Mock
    private RequestService requestService;

    @Mock
    private AviationAerCorsiaAnnualOffsettingValidatorService validatorService;

    @Mock
    private DecisionNotificationUsersValidator usersValidator;

    @Mock
    private RequestTask requestTask;

    @Mock
    private Request request;


    @Mock
    private AppUser appUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testApplySaveAction() {
        AviationAerCorsiaAnnualOffsettingSaveRequestTaskActionPayload taskActionPayload = mock(AviationAerCorsiaAnnualOffsettingSaveRequestTaskActionPayload.class);
        AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload taskPayload = mock(AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload.class);

        when(requestTask.getPayload()).thenReturn(taskPayload);
        service.applySaveAction(requestTask, taskActionPayload);

        verify(taskPayload).setAviationAerCorsiaAnnualOffsetting(taskActionPayload.getAviationAerCorsiaAnnualOffsetting());
        verify(taskPayload).setAviationAerCorsiaAnnualOffsettingSectionsCompleted(taskActionPayload.getAviationAerCorsiaAnnualOffsettingSectionsCompleted());
    }

    @Test
    void testCancel() {
        Request request1 = Request.builder()
                .payload(AviationAerCorsiaAnnualOffsettingRequestPayload.builder()
                        .regulatorAssignee("user1").build()).build();

        String requestId = "requestId";
        when(requestService.findRequestById(requestId)).thenReturn(request1);
        service.cancel(requestId);
        verify(requestService).addActionToRequest(request1, null,
                RequestActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_CANCELLED,
                request1.getPayload().getRegulatorAssignee());
    }

    @Test
    void testRequestPeerReview() {
        String peerReviewer = "peerReviewer";
        String appUserId = "appUserId";
        AviationAerCorsiaAnnualOffsettingRequestPayload requestPayload = mock(AviationAerCorsiaAnnualOffsettingRequestPayload.class);
        AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload requestTaskPayload = mock(AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload.class);
        AviationAerCorsiaAnnualOffsetting aviationAerCorsiaAnnualOffsetting = mock(AviationAerCorsiaAnnualOffsetting.class);

        when(requestTask.getRequest()).thenReturn(request);
        when(request.getPayload()).thenReturn(requestPayload);
        when(requestTask.getPayload()).thenReturn(requestTaskPayload);
        when(requestTaskPayload.getAviationAerCorsiaAnnualOffsetting()).thenReturn(aviationAerCorsiaAnnualOffsetting);
        when(appUser.getUserId()).thenReturn(appUserId);

        service.requestPeerReview(requestTask, peerReviewer, appUser);

        verify(requestPayload).setRegulatorPeerReviewer(peerReviewer);
        verify(requestPayload).setRegulatorReviewer(appUserId);
        verify(requestPayload).setAviationAerCorsiaAnnualOffsetting(aviationAerCorsiaAnnualOffsetting);
        verify(requestPayload).setAviationAerCorsiaAnnualOffsettingSectionsCompleted(requestTaskPayload.getAviationAerCorsiaAnnualOffsettingSectionsCompleted());
    }

    @Test
    void testGetRequestType() {
        RequestType result = service.getRequestType();
        Assert.assertEquals(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING, result);
    }

    @Test
    void testApplySubmitNotify_success() {
        AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload requestTaskPayload = mock(AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload.class);
         AviationAerCorsiaAnnualOffsettingRequestMetadata requestMetadata = mock(AviationAerCorsiaAnnualOffsettingRequestMetadata.class);
        AviationAerCorsiaAnnualOffsetting aviationAerCorsiaAnnualOffsetting = mock(AviationAerCorsiaAnnualOffsetting.class);
        DecisionNotification decisionNotification = mock(DecisionNotification.class);
        AviationAerCorsiaAnnualOffsettingRequestPayload requestPayload = mock(AviationAerCorsiaAnnualOffsettingRequestPayload.class);

        when(requestTask.getPayload()).thenReturn(requestTaskPayload);
        when(requestTaskPayload.getAviationAerCorsiaAnnualOffsetting()).thenReturn(aviationAerCorsiaAnnualOffsetting);
        when(requestTask.getRequest()).thenReturn(request);
        when(request.getPayload()).thenReturn(requestPayload);
        when(request.getMetadata()).thenReturn(requestMetadata);
        when(usersValidator.areUsersValid(requestTask, decisionNotification, appUser)).thenReturn(true);

        service.applySubmitNotify(requestTask, decisionNotification, appUser);

        verify(validatorService).validateAviationAerCorsiaAnnualOffsetting(aviationAerCorsiaAnnualOffsetting);
        verify(requestPayload).setDecisionNotification(decisionNotification);
        verify(requestPayload).setAviationAerCorsiaAnnualOffsetting(aviationAerCorsiaAnnualOffsetting);
        verify(requestPayload).setAviationAerCorsiaAnnualOffsettingSectionsCompleted(requestTaskPayload.getAviationAerCorsiaAnnualOffsettingSectionsCompleted());
    }

    @Test
    void testApplySubmitNotify_validationFailure() {
        AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload requestTaskPayload = mock(AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload.class);
        AviationAerCorsiaAnnualOffsetting aviationAerCorsiaAnnualOffsetting = mock(AviationAerCorsiaAnnualOffsetting.class);
        DecisionNotification decisionNotification = mock(DecisionNotification.class);

        when(requestTask.getPayload()).thenReturn(requestTaskPayload);
        when(requestTaskPayload.getAviationAerCorsiaAnnualOffsetting()).thenReturn(aviationAerCorsiaAnnualOffsetting);
        when(usersValidator.areUsersValid(requestTask, decisionNotification, appUser)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                service.applySubmitNotify(requestTask, decisionNotification, appUser));

        assertEquals(ErrorCode.FORM_VALIDATION, exception.getErrorCode());
        verify(validatorService).validateAviationAerCorsiaAnnualOffsetting(aviationAerCorsiaAnnualOffsetting);
    }
}
