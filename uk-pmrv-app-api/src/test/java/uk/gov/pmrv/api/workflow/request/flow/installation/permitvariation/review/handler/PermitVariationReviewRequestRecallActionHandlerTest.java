package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType.PERMIT_VARIATION_RECALLED_FROM_AMENDS;

@ExtendWith(MockitoExtension.class)
class PermitVariationReviewRequestRecallActionHandlerTest {

    @InjectMocks
    private PermitVariationReviewRequestRecallActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private RequestService requestService;

    @Test
    void process() {

        final Long requestTaskId = 1L;
        final AppUser appUser = AppUser.builder().userId("userId").build();

        final Request build = Request.builder().id("2").build();
        final RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(build)
            .processTaskId("processTaskId")
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(
            requestTaskId,
            RequestTaskActionType.PERMIT_VARIATION_RECALL_FROM_AMENDS,
            appUser,
            new RequestTaskActionEmptyPayload());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(),
                null,
                PERMIT_VARIATION_RECALLED_FROM_AMENDS,
                appUser.getUserId());
        verify(workflowService, times(1)).completeTask("processTaskId");
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_VARIATION_RECALL_FROM_AMENDS);
    }

    @Test
    void getRequestActionType() {
        assertEquals(RequestActionType.PERMIT_VARIATION_RECALLED_FROM_AMENDS, handler.getRequestActionType());
    }
}
