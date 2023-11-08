package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowances;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesSaveApplicationRequestTaskActionPayload;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnOfAllowancesServiceTest {

    @Mock
    private RequestTask requestTask;

    @InjectMocks
    private ReturnOfAllowancesService service;

    @Test
    void applySavePayload() {
        ReturnOfAllowancesSaveApplicationRequestTaskActionPayload actionPayload =
            ReturnOfAllowancesSaveApplicationRequestTaskActionPayload.builder()
                .returnOfAllowances(ReturnOfAllowances.builder().build())
                .sectionsCompleted(Map.of())
                .build();
        ReturnOfAllowancesApplicationSubmitRequestTaskPayload taskPayload =
            ReturnOfAllowancesApplicationSubmitRequestTaskPayload.builder().build();

        when(requestTask.getPayload()).thenReturn(taskPayload);

        service.applySavePayload(actionPayload, requestTask);

        assertEquals(taskPayload.getSectionsCompleted(), actionPayload.getSectionsCompleted());
        assertEquals(taskPayload.getReturnOfAllowances(), actionPayload.getReturnOfAllowances());
    }

    @Test
    void saveReturnOfAllowancesDecisionNotification() {
        ReturnOfAllowancesRequestPayload requestPayload = ReturnOfAllowancesRequestPayload.builder().build();
        Request request = Request.builder()
            .payload(requestPayload)
            .build();
        DecisionNotification decisionNotification = DecisionNotification.builder().build();
        NotifyOperatorForDecisionRequestTaskActionPayload actionPayload =
            NotifyOperatorForDecisionRequestTaskActionPayload.builder().decisionNotification(decisionNotification).build();
        ReturnOfAllowancesApplicationSubmitRequestTaskPayload taskPayload =
            ReturnOfAllowancesApplicationSubmitRequestTaskPayload.builder()
                .sectionsCompleted(Map.of())
                .returnOfAllowances(ReturnOfAllowances.builder().build())
                .build();

        when(requestTask.getPayload()).thenReturn(taskPayload);
        when(requestTask.getRequest()).thenReturn(request);

        service.saveReturnOfAllowancesDecisionNotification(actionPayload, requestTask);

        assertEquals(requestPayload.getDecisionNotification(), actionPayload.getDecisionNotification());
        assertEquals(requestPayload.getReturnOfAllowancesSectionsCompleted(), taskPayload.getSectionsCompleted());
        assertEquals(requestPayload.getReturnOfAllowances(), taskPayload.getReturnOfAllowances());
        assertNotNull(request.getSubmissionDate());
    }

    @Test
    void requestPeerReview() {
        ReturnOfAllowancesRequestPayload requestPayload = ReturnOfAllowancesRequestPayload.builder().build();
        Request request = Request.builder()
            .payload(requestPayload)
            .build();
        ReturnOfAllowancesApplicationSubmitRequestTaskPayload taskPayload =
            ReturnOfAllowancesApplicationSubmitRequestTaskPayload.builder()
                .sectionsCompleted(Map.of())
                .returnOfAllowances(ReturnOfAllowances.builder().build())
                .build();

        when(requestTask.getPayload()).thenReturn(taskPayload);
        when(requestTask.getRequest()).thenReturn(request);

        service.requestPeerReview(requestTask, "selectedPeerReviewer", "regulatorReviewer");

        assertEquals(requestPayload.getReturnOfAllowancesSectionsCompleted(), taskPayload.getSectionsCompleted());
        assertEquals(requestPayload.getReturnOfAllowances(), taskPayload.getReturnOfAllowances());
        assertEquals(requestPayload.getRegulatorPeerReviewer(), "selectedPeerReviewer");
        assertEquals(requestPayload.getRegulatorReviewer(), "regulatorReviewer");
    }
}
