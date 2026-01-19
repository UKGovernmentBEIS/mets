package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request.AviationAccountRegistryManualPushAvailabilityService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationAccountCreatedRegistryEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service.EmpIssuanceRegistryIntegrationAddRequestActionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceReviewManualRegistryAccountOpeningEventActionHandlerTest {

    @Mock private RequestTaskService requestTaskService;
    @Mock private AviationAccountRegistryManualPushAvailabilityService availabilityService;
    @Mock private ApplicationEventPublisher publisher;
    @Mock private EmpIssuanceRegistryIntegrationAddRequestActionService addRequestActionService;

    @InjectMocks
    private EmpIssuanceReviewManualRegistryAccountOpeningEventActionHandler openingEventActionHandler;

    @Test
    void process_available_publishesEvent_and_addsRequestAction() {
        Long requestTaskId = 1L;
        String requestId = "REQ-1";
        Long accountId = 100L;

        EmissionsMonitoringPlanUkEts emp = mock(EmissionsMonitoringPlanUkEts.class);
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload taskPayload =
                EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
                        .emissionsMonitoringPlan(emp)
                        .build();

        Request request = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .type(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW)
                .request(request)
                .payload(taskPayload)
                .build();

        AppUser appUser = mock(AppUser.class);

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(availabilityService.isManualPushAvailable(requestId)).thenReturn(true);

        ArgumentCaptor<AviationAccountCreatedRegistryEvent> eventCaptor = ArgumentCaptor.forClass(AviationAccountCreatedRegistryEvent.class);
        ArgumentCaptor<AviationAccountCreatedRegistryEvent> dtoCaptor =
                ArgumentCaptor.forClass(AviationAccountCreatedRegistryEvent.class);

        openingEventActionHandler.process(requestTaskId, RequestTaskActionType.EMP_ISSUANCE_UKETS_MANUAL_ACCOUNT_OPENING_REGISTRY, appUser, null);

        verify(requestTaskService).findTaskById(requestTaskId);
        verify(availabilityService).isManualPushAvailable(requestId);

        verify(publisher).publishEvent(eventCaptor.capture());
        AviationAccountCreatedRegistryEvent published = eventCaptor.getValue();
        assertEquals(requestId, published.getRequestId());
        assertEquals(accountId, published.getAccountId());
        assertEquals(emp, published.getEmissionsMonitoringPlan());


        verifyNoMoreInteractions(publisher);
    }

    @Test
    void process_notAvailable_throwsBusinessException_andDoesNothingElse() {
        Long requestTaskId = 2L;
        String requestId = "REQ-2";
        Long accountId = 200L;

        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload taskPayload =
                EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder().build();

        Request request = Request.builder()
                .id(requestId)
                .accountId(accountId)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .request(request)
                .payload(taskPayload)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(availabilityService.isManualPushAvailable(requestId)).thenReturn(false);

        assertThrows(BusinessException.class, () ->
                openingEventActionHandler.process(requestTaskId, RequestTaskActionType.EMP_ISSUANCE_UKETS_MANUAL_ACCOUNT_OPENING_REGISTRY, mock(AppUser.class), null));

        verify(requestTaskService).findTaskById(requestTaskId);
        verify(availabilityService).isManualPushAvailable(requestId);
        verifyNoInteractions(publisher, addRequestActionService);
    }

    @Test
    void getTypes_returnsExpected() {
        List<RequestTaskActionType> types = openingEventActionHandler.getTypes();
        assertEquals(1, types.size());
        assertTrue(types.contains(RequestTaskActionType.EMP_ISSUANCE_UKETS_MANUAL_ACCOUNT_OPENING_REGISTRY));
    }
}