package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDetermination;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDeterminationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDeterminationSaveApplicationRequestTaskActionPayload;

@ExtendWith(MockitoExtension.class)
class NonComplianceFinalDeterminationApplyServiceTest {

    @InjectMocks
    private NonComplianceFinalDeterminationApplyService service;

    @Test
    void applySaveAction() {

        final NonComplianceFinalDeterminationRequestTaskPayload taskPayload =
            NonComplianceFinalDeterminationRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        final NonComplianceFinalDeterminationSaveApplicationRequestTaskActionPayload taskActionPayload =
            NonComplianceFinalDeterminationSaveApplicationRequestTaskActionPayload.builder()
                .finalDetermination(NonComplianceFinalDetermination.builder()
                .complianceRestored(true)
                .complianceRestoredDate(LocalDate.of(2021, 1, 2))
                .comments("comments")
                .reissuePenalty(true)
                .operatorPaid(true)
                .operatorPaidDate(LocalDate.of(2021, 2, 5))
                .build())
                .determinationCompleted(true)
                .build();

        service.applySaveAction(requestTask, taskActionPayload);

        assertEquals(taskPayload.getFinalDetermination(), taskActionPayload.getFinalDetermination());
        assertEquals(taskPayload.getDeterminationCompleted(), taskActionPayload.getDeterminationCompleted());
    }
}
