package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltyRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltySaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

@ExtendWith(MockitoExtension.class)
class NonComplianceCivilPenaltyApplyServiceTest {

    @InjectMocks
    private NonComplianceCivilPenaltyApplyService service;

    @Test
    void applySaveAction() {

        final NonComplianceCivilPenaltyRequestTaskPayload taskPayload =
            NonComplianceCivilPenaltyRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        final UUID civilPenalty = UUID.randomUUID();
        final NonComplianceCivilPenaltySaveApplicationRequestTaskActionPayload taskActionPayload =
            NonComplianceCivilPenaltySaveApplicationRequestTaskActionPayload.builder()
                .civilPenalty(civilPenalty)
                .comments("comments")
                .civilPenaltyCompleted(true)
                .build();

        service.applySaveAction(requestTask, taskActionPayload);

        assertEquals(taskPayload.getCivilPenalty(), taskActionPayload.getCivilPenalty());
        assertEquals(taskPayload.getComments(), taskActionPayload.getComments());
        assertEquals(taskPayload.getCivilPenaltyCompleted(), taskActionPayload.getCivilPenaltyCompleted());
    }



    @Test
    void saveRequestPeerReviewAction() {

        final UUID civilPenalty = UUID.randomUUID();
        final LocalDate dueDate = LocalDate.of(2022, 1, 1);
        final NonComplianceCivilPenaltyRequestTaskPayload taskPayload =
            NonComplianceCivilPenaltyRequestTaskPayload.builder()
                .civilPenalty(civilPenalty)
                .penaltyAmount("penaltyAmount")
                .dueDate(dueDate)
                .comments("comments")
                .nonComplianceAttachments(Map.of(civilPenalty, "civilPenalty"))
                .build();
        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).request(Request.builder()
            .payload(requestPayload).build()).build();
        final String peerReviewer = "peerReviewer";

        service.saveRequestPeerReviewAction(requestTask, peerReviewer);

        assertEquals(taskPayload.getCivilPenalty(), requestPayload.getCivilPenalty());
        assertEquals(taskPayload.getPenaltyAmount(), requestPayload.getCivilPenaltyAmount());
        assertEquals(taskPayload.getDueDate(), requestPayload.getCivilPenaltyDueDate());
        assertEquals(taskPayload.getComments(), requestPayload.getCivilPenaltyComments());
        assertEquals(taskPayload.getNonComplianceAttachments(), requestPayload.getNonComplianceAttachments());
        assertEquals(peerReviewer, requestPayload.getRegulatorPeerReviewer());

    }
}
