package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowances;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesSaveApplicationRequestTaskActionPayload;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithholdingOfAllowancesServiceTest {

    @Mock
    private RequestTask requestTask;

    @InjectMocks
    private WithholdingOfAllowancesService service;

    @Test
    void applySavePayload() {
        WithholdingOfAllowancesSaveApplicationRequestTaskActionPayload actionPayload =
            WithholdingOfAllowancesSaveApplicationRequestTaskActionPayload.builder()
                .withholdingOfAllowances(WithholdingOfAllowances.builder().build())
                .sectionsCompleted(Map.of())
                .build();
        WithholdingOfAllowancesApplicationSubmitRequestTaskPayload taskPayload =
            WithholdingOfAllowancesApplicationSubmitRequestTaskPayload.builder().build();

        when(requestTask.getPayload()).thenReturn(taskPayload);

        service.applySavePayload(actionPayload, requestTask);

        assertEquals(taskPayload.getSectionsCompleted(), actionPayload.getSectionsCompleted());
        assertEquals(taskPayload.getWithholdingOfAllowances(), actionPayload.getWithholdingOfAllowances());
    }

    @Test
    void saveWithholdingOfAllowancesDecisionNotification() {
        WithholdingOfAllowancesRequestPayload requestPayload = WithholdingOfAllowancesRequestPayload.builder().build();
        Request request = Request.builder()
            .payload(requestPayload)
            .build();
        DecisionNotification decisionNotification = DecisionNotification.builder().build();
        NotifyOperatorForDecisionRequestTaskActionPayload actionPayload =
            NotifyOperatorForDecisionRequestTaskActionPayload.builder().decisionNotification(decisionNotification).build();
        WithholdingOfAllowancesApplicationSubmitRequestTaskPayload taskPayload =
            WithholdingOfAllowancesApplicationSubmitRequestTaskPayload.builder()
                .sectionsCompleted(Map.of())
                .withholdingOfAllowances(WithholdingOfAllowances.builder().build())
                .build();

        when(requestTask.getPayload()).thenReturn(taskPayload);
        when(requestTask.getRequest()).thenReturn(request);

        service.saveWithholdingOfAllowancesDecisionNotification(actionPayload, requestTask);

        assertEquals(requestPayload.getDecisionNotification(), actionPayload.getDecisionNotification());
        assertEquals(requestPayload.getWithholdingOfAllowancesSectionsCompleted(), taskPayload.getSectionsCompleted());
        assertEquals(requestPayload.getWithholdingOfAllowances(), taskPayload.getWithholdingOfAllowances());
        assertNotNull(request.getSubmissionDate());
    }

    @Test
    void requestPeerReview() {
        WithholdingOfAllowancesRequestPayload requestPayload = WithholdingOfAllowancesRequestPayload.builder().build();
        Request request = Request.builder()
            .payload(requestPayload)
            .build();
        WithholdingOfAllowancesApplicationSubmitRequestTaskPayload taskPayload =
            WithholdingOfAllowancesApplicationSubmitRequestTaskPayload.builder()
                .sectionsCompleted(Map.of())
                .withholdingOfAllowances(WithholdingOfAllowances.builder().build())
                .build();

        when(requestTask.getPayload()).thenReturn(taskPayload);
        when(requestTask.getRequest()).thenReturn(request);

        service.requestPeerReview(requestTask, "selectedPeerReviewer", "regulatorReviewer");

        assertEquals(requestPayload.getWithholdingOfAllowancesSectionsCompleted(), taskPayload.getSectionsCompleted());
        assertEquals(requestPayload.getWithholdingOfAllowances(), taskPayload.getWithholdingOfAllowances());
        assertEquals(requestPayload.getRegulatorPeerReviewer(), "selectedPeerReviewer");
        assertEquals(requestPayload.getRegulatorReviewer(), "regulatorReviewer");
    }
}
