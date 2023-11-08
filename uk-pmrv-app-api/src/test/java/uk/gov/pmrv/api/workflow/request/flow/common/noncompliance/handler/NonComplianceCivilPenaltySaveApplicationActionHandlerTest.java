package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltySaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service.NonComplianceCivilPenaltyApplyService;

@ExtendWith(MockitoExtension.class) 
class NonComplianceCivilPenaltySaveApplicationActionHandlerTest {

    @InjectMocks
    private NonComplianceCivilPenaltySaveApplicationActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NonComplianceCivilPenaltyApplyService applyService;

    @Test
    void process() {

        final long requestTaskId = 1L;
        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        final NonComplianceCivilPenaltySaveApplicationRequestTaskActionPayload taskActionPayload =
            NonComplianceCivilPenaltySaveApplicationRequestTaskActionPayload.builder().build();
        final PmrvUser pmrvUser = PmrvUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId,
            RequestTaskActionType.NON_COMPLIANCE_CIVIL_PENALTY_SAVE_APPLICATION,
            pmrvUser,
            taskActionPayload
        );

        verify(applyService, times(1)).applySaveAction(requestTask, taskActionPayload);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(
            RequestTaskActionType.NON_COMPLIANCE_CIVIL_PENALTY_SAVE_APPLICATION);
    }
}
