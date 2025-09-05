package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceAmendDetailsRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service.NonComplianceApplyService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NonComplianceAmendDetailsActionsHandlerTest {

    @InjectMocks
    private NonComplianceAmendDetailsActionsHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NonComplianceApplyService nonComplianceApplyService;

    @Test
    void process() {

        final long requestTaskId = 1L;
        final RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();

        final NonComplianceAmendDetailsRequestTaskActionPayload taskActionPayload =
            NonComplianceAmendDetailsRequestTaskActionPayload.builder().build();
        final AppUser appUser = AppUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId,
            RequestTaskActionType.NON_COMPLIANCE_AMEND_DETAILS,
            appUser,
            taskActionPayload
        );

        verify(nonComplianceApplyService, times(1)).amendDetails(requestTask, taskActionPayload, appUser);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(
            RequestTaskActionType.NON_COMPLIANCE_AMEND_DETAILS);
    }

}
