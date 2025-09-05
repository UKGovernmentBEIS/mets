package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service.HSETIRegulatorReviewSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.validation.HSETIRequestPeerReviewValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HSETIApplicationPeerReviewActionHandlerTest {

    @InjectMocks
    private HSETIApplicationPeerReviewActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private HSETIRequestPeerReviewValidator validator;

    @Mock
    private RequestService requestService;

    @Mock
    private HSETIRegulatorReviewSubmitService hsetiRegulatorReviewSubmitService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final Long requestTaskId = 1L;
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final String selectedPeerReviewer = "selectedPeerReviewer";
        final PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer(selectedPeerReviewer)
                .payloadType(RequestTaskActionPayloadType.HSE_TI_REQUEST_PEER_REVIEW_PAYLOAD)
                .build();
        final Request request = Request.builder().id("2").build();
        final RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .request(request)
                .processTaskId("processTaskId")
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(
                requestTaskId,
                RequestTaskActionType.HSE_TI_REQUEST_PEER_REVIEW,
                appUser,
                taskActionPayload);

        verify(requestTaskService).findTaskById(requestTaskId);
        verify(validator).validate(requestTask, taskActionPayload, appUser);
        verify(requestService).addActionToRequest(request, null, RequestActionType.HSE_TI_APPLICATION_PEER_REVIEW_REQUESTED, appUser.getUserId());
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.HSE_TI_REQUEST_PEER_REVIEW);
    }
}
