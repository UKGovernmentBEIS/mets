package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCloseApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCloseJustification;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonCompliancePenalties;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceReason;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceSaveApplicationRequestTaskActionPayload;

@ExtendWith(MockitoExtension.class)
class NonComplianceApplyServiceTest {

    @InjectMocks
    private NonComplianceApplyService service;

    @Test
    void applyCloseAction() {

        final UUID file = UUID.randomUUID();
        final String reason = "the reason";
        final NonComplianceCloseApplicationRequestTaskActionPayload taskActionPayload =
            NonComplianceCloseApplicationRequestTaskActionPayload.builder()
                .closeJustification(NonComplianceCloseJustification.builder()
                    .reason(reason)
                    .files(Set.of(file))
                    .build())
                .build();

        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder().build();
        final Request request = Request.builder().payload(requestPayload).build();

        final Map<UUID, String> nonComplianceAttachments = Map.of(file, "filename");
        final NonComplianceApplicationSubmitRequestTaskPayload requestTaskPayload =
            NonComplianceApplicationSubmitRequestTaskPayload
                .builder()
                .nonComplianceAttachments(nonComplianceAttachments)
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .request(request)
            .payload(requestTaskPayload)
            .build();

        service.applyCloseAction(requestTask, taskActionPayload);

        assertEquals(requestTaskPayload.getCloseJustification(), taskActionPayload.getCloseJustification());
        assertEquals(requestPayload.getCloseJustification(), taskActionPayload.getCloseJustification());
        assertEquals(requestPayload.getNonComplianceAttachments(), nonComplianceAttachments);
    }

    @Test
    void applySaveAction() {

        final NonComplianceSaveApplicationRequestTaskActionPayload taskActionPayload =
            NonComplianceSaveApplicationRequestTaskActionPayload.builder()
                .reason(NonComplianceReason.CARRYING_OUT_A_REGULATED_ACTIVITY_WITHOUT_A_PERMIT)
                .nonComplianceDate(LocalDate.of(2021, 1, 1))
                .complianceDate(LocalDate.of(2022, 1, 1))
                .comments("comments")
                .selectedRequests(Set.of("reqId"))
                .nonCompliancePenalties(NonCompliancePenalties.builder()
                    .civilPenalty(true)
                    .noticeOfIntent(false)
                    .dailyPenalty(false).build())
                .sectionCompleted(true)
                .build();

        final NonComplianceApplicationSubmitRequestTaskPayload taskPayload =
            NonComplianceApplicationSubmitRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .build();

        service.applySaveAction(requestTask, taskActionPayload);

        assertEquals(taskPayload.getReason(), taskActionPayload.getReason());
        assertEquals(taskPayload.getNonComplianceDate(), taskActionPayload.getNonComplianceDate());
        assertEquals(taskPayload.getComplianceDate(), taskActionPayload.getComplianceDate());
        assertEquals(taskPayload.getComments(), taskActionPayload.getComments());
        assertEquals(taskPayload.getSelectedRequests(), taskActionPayload.getSelectedRequests());
        assertEquals(taskPayload.getNonCompliancePenalties(), taskActionPayload.getNonCompliancePenalties());
        assertEquals(taskPayload.getSectionCompleted(), taskActionPayload.getSectionCompleted());
    }
}
