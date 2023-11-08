package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNoticeOfIntentRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNoticeOfIntentSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

@ExtendWith(MockitoExtension.class)
class NonComplianceNoticeOfIntentApplyServiceTest {

    @InjectMocks
    private NonComplianceNoticeOfIntentApplyService service;

    @Test
    void applySaveAction() {

        final NonComplianceNoticeOfIntentRequestTaskPayload taskPayload =
            NonComplianceNoticeOfIntentRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        final UUID noticeOfIntent = UUID.randomUUID();
        final NonComplianceNoticeOfIntentSaveApplicationRequestTaskActionPayload taskActionPayload =
            NonComplianceNoticeOfIntentSaveApplicationRequestTaskActionPayload.builder()
                .noticeOfIntent(noticeOfIntent)
                .comments("comments")
                .noticeOfIntentCompleted(true)
                .build();

        service.applySaveAction(requestTask, taskActionPayload);

        assertEquals(taskPayload.getNoticeOfIntent(), taskActionPayload.getNoticeOfIntent());
        assertEquals(taskPayload.getComments(), taskActionPayload.getComments());
        assertEquals(taskPayload.getNoticeOfIntentCompleted(), taskActionPayload.getNoticeOfIntentCompleted());
    }

    @Test
    void saveRequestPeerReviewAction() {

        final UUID noticeOfIntent = UUID.randomUUID();
        final NonComplianceNoticeOfIntentRequestTaskPayload taskPayload =
            NonComplianceNoticeOfIntentRequestTaskPayload.builder()
                .noticeOfIntent(noticeOfIntent)
                .comments("comments")
                .nonComplianceAttachments(Map.of(noticeOfIntent, "noticeOfIntent"))
                .build();
        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).request(Request.builder()
            .payload(requestPayload).build()).build();
        final String peerReviewer = "peerReviewer";

        service.saveRequestPeerReviewAction(requestTask, peerReviewer);

        assertEquals(taskPayload.getNoticeOfIntent(), requestPayload.getNoticeOfIntent());
        assertEquals(taskPayload.getComments(), requestPayload.getNoticeOfIntentComments());
        assertEquals(taskPayload.getNonComplianceAttachments(), requestPayload.getNonComplianceAttachments());
        assertEquals(peerReviewer, requestPayload.getRegulatorPeerReviewer());

    }
}
