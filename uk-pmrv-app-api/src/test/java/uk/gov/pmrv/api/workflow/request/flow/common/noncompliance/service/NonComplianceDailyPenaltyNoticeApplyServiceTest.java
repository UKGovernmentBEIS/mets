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
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDailyPenaltyNoticeRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDailyPenaltyNoticeSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

@ExtendWith(MockitoExtension.class)
class NonComplianceDailyPenaltyNoticeApplyServiceTest {

    @InjectMocks
    private NonComplianceDailyPenaltyNoticeApplyService service;

    @Test
    void applySaveAction() {

        final NonComplianceDailyPenaltyNoticeRequestTaskPayload taskPayload =
            NonComplianceDailyPenaltyNoticeRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        final UUID dailyPenaltyNotice = UUID.randomUUID();
        final NonComplianceDailyPenaltyNoticeSaveApplicationRequestTaskActionPayload taskActionPayload =
            NonComplianceDailyPenaltyNoticeSaveApplicationRequestTaskActionPayload.builder()
                .dailyPenaltyNotice(dailyPenaltyNotice)
                .comments("comments")
                .dailyPenaltyCompleted(true)
                .build();

        service.applySaveAction(requestTask, taskActionPayload);

        assertEquals(taskPayload.getDailyPenaltyNotice(), taskActionPayload.getDailyPenaltyNotice());
        assertEquals(taskPayload.getComments(), taskActionPayload.getComments());
        assertEquals(taskPayload.getDailyPenaltyCompleted(), taskActionPayload.getDailyPenaltyCompleted());
    }
    
    @Test
    void saveRequestPeerReviewAction() {

        final UUID dailyPenaltyNotice = UUID.randomUUID();
        final NonComplianceDailyPenaltyNoticeRequestTaskPayload taskPayload =
            NonComplianceDailyPenaltyNoticeRequestTaskPayload.builder()
                .dailyPenaltyNotice(dailyPenaltyNotice)
                .comments("comments")
                .nonComplianceAttachments(Map.of(dailyPenaltyNotice, "penalty"))
                .build();
        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).request(Request.builder()
            .payload(requestPayload).build()).build();
        final String peerReviewer = "peerReviewer";

        service.saveRequestPeerReviewAction(requestTask, peerReviewer);

        assertEquals(taskPayload.getDailyPenaltyNotice(), requestPayload.getDailyPenaltyNotice());
        assertEquals(taskPayload.getComments(), requestPayload.getDailyPenaltyComments());
        assertEquals(taskPayload.getNonComplianceAttachments(), requestPayload.getNonComplianceAttachments());
        assertEquals(peerReviewer, requestPayload.getRegulatorPeerReviewer());

    }
}
