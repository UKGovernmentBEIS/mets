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
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDeterminationSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service.NonComplianceFinalDeterminationApplyService;

@ExtendWith(MockitoExtension.class)
class NonComplianceFinalDeterminationSaveApplicationActionHandlerTest {

    @InjectMocks
    private NonComplianceFinalDeterminationSaveApplicationActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NonComplianceFinalDeterminationApplyService applyService;

    @Test
    void process() {

        final long requestTaskId = 1L;
        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        final NonComplianceFinalDeterminationSaveApplicationRequestTaskActionPayload taskActionPayload =
            NonComplianceFinalDeterminationSaveApplicationRequestTaskActionPayload.builder().build();
        final AppUser appUser = AppUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId,
            RequestTaskActionType.NON_COMPLIANCE_FINAL_DETERMINATION_SAVE_APPLICATION,
            appUser,
            taskActionPayload
        );

        verify(applyService, times(1)).applySaveAction(requestTask, taskActionPayload);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(
            RequestTaskActionType.NON_COMPLIANCE_FINAL_DETERMINATION_SAVE_APPLICATION);
    }
}
