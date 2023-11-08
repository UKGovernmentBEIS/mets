package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturned;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturnedSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.validator.ReturnOfAllowancesReturnedValidator;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnOfAllowancesReturnedServiceTest {

    @Mock
    private RequestTask requestTask;

    @Mock
    private RequestService requestService;

    @Mock
    private ReturnOfAllowancesReturnedValidator returnOfAllowancesReturnedValidator;

    @InjectMocks
    private ReturnOfAllowancesReturnedService service;

    @Test
    void applySavePayload() {
        ReturnOfAllowancesReturnedSaveApplicationRequestTaskActionPayload actionPayload =
            ReturnOfAllowancesReturnedSaveApplicationRequestTaskActionPayload.builder()
                .returnOfAllowancesReturned(ReturnOfAllowancesReturned.builder().build())
                .sectionsCompleted(Map.of())
                .build();
        ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload taskPayload =
            ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload.builder().build();

        when(requestTask.getPayload()).thenReturn(taskPayload);

        service.applySavePayload(actionPayload, requestTask);

        assertEquals(taskPayload.getSectionsCompleted(), actionPayload.getSectionsCompleted());
        assertEquals(taskPayload.getReturnOfAllowancesReturned(), actionPayload.getReturnOfAllowancesReturned());
    }

    @Test
    void submit() {
        ReturnOfAllowancesRequestPayload requestPayload = ReturnOfAllowancesRequestPayload.builder().build();
        Request request = Request.builder()
            .payload(requestPayload)
            .build();

        ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload actionPayload =
            ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload.builder()
                .returnOfAllowancesReturned(ReturnOfAllowancesReturned.builder().build())
                .payloadType(RequestActionPayloadType.RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_COMPLETED_PAYLOAD)
                .build();
        ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload taskPayload =
            ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload.builder()
                .sectionsCompleted(Map.of())
                .returnOfAllowancesReturned(ReturnOfAllowancesReturned.builder().build())
                .build();

        when(requestTask.getPayload()).thenReturn(taskPayload);
        when(requestTask.getRequest()).thenReturn(request);

        service.submit(requestTask);

        assertEquals(requestPayload.getReturnOfAllowancesReturnedSectionsCompleted(), taskPayload.getSectionsCompleted());
        assertEquals(requestPayload.getReturnOfAllowancesReturned(), taskPayload.getReturnOfAllowancesReturned());

        verify(returnOfAllowancesReturnedValidator).validate(requestPayload.getReturnOfAllowancesReturned());
        verify(requestService).addActionToRequest(
            request,
            actionPayload,
            RequestActionType.RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_COMPLETED,
            requestPayload.getRegulatorAssignee()
        );
    }

}