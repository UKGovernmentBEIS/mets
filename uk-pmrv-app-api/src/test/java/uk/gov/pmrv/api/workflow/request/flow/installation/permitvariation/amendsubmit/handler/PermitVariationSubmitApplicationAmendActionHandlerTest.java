package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.service.PermitVariationAmendService;

@ExtendWith(MockitoExtension.class)
class PermitVariationSubmitApplicationAmendActionHandlerTest {

    @InjectMocks
    private PermitVariationSubmitApplicationAmendActionHandler submitApplicationAmendActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitVariationAmendService permitVariationAmendService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String processTaskId = "123";
        PermitVariationApplicationSubmitApplicationAmendRequestTaskActionPayload submitAmendRequestTaskActionPayload =
            PermitVariationApplicationSubmitApplicationAmendRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_VARIATION_SUBMIT_APPLICATION_AMEND_PAYLOAD)
                .permitSectionsCompleted(Map.of(
                    "installationCategory", List.of(true),
                    "installationOperatorDetails", List.of(true)
                ))
                .build();

        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().build())
            .processTaskId(processTaskId)
            .build();
        AppUser appUser = AppUser.builder().userId("user").build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        submitApplicationAmendActionHandler
            .process(requestTask.getId(), RequestTaskActionType.PERMIT_VARIATION_SUBMIT_APPLICATION_AMEND, appUser, submitAmendRequestTaskActionPayload);

        //verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitVariationAmendService, times(1))
            .submitAmendedPermitVariation(submitAmendRequestTaskActionPayload, requestTask);
        verify(requestService, times(1)).addActionToRequest(
            requestTask.getRequest(),
            null,
            RequestActionType.PERMIT_VARIATION_APPLICATION_AMENDS_SUBMITTED,
            appUser.getUserId());
        verify(workflowService, times(1)).completeTask(processTaskId);
    }

    @Test
    void getTypes() {
        assertThat(submitApplicationAmendActionHandler.getTypes())
            .containsExactly(RequestTaskActionType.PERMIT_VARIATION_SUBMIT_APPLICATION_AMEND);
    }
}