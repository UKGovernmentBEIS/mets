package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.enumeration.AviationAerCorsiaAnnualOffsettingSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.service.AviationAerCorsiaAnnualOffsettingRequestPeerReviewValidator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service.AviationAerCorsiaAnnualOffsettingSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AviationAerCorsiaAnnualOffsettingPeerReviewActionHandlerTest {

    @InjectMocks
    private AviationAerCorsiaAnnualOffsettingPeerReviewActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private AviationAerCorsiaAnnualOffsettingSubmitService aviationAerCorsiaAnnualOffsettingSubmitService;

    @Mock
    private AviationAerCorsiaAnnualOffsettingRequestPeerReviewValidator peerReviewValidator;

    @Mock
    private RequestTask requestTask;

    @Mock
    private Request request;

    @Mock
    private AppUser appUser;

    @Spy
    private List<AviationAerCorsiaAnnualOffsettingSubmitService> aviationAerCorsiaAnnualOffsettingSubmitServices;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(requestTask.getRequest()).thenReturn(request);
    }

    @Test
    void testProcess_success() {
        PeerReviewRequestTaskActionPayload taskActionPayload =
                PeerReviewRequestTaskActionPayload.builder().peerReviewer("peerReviewer").build();


        final AppUser appUser = AppUser.builder().userId("peerReviewer").build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final RequestTask requestTask = RequestTask.builder().id(1L).request(request).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTask.getId(), RequestTaskActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_REQUEST_PEER_REVIEW, appUser,
                taskActionPayload);

        verify(peerReviewValidator, times(1)).validate(requestTask, taskActionPayload, appUser);
        verify(aviationAerCorsiaAnnualOffsettingSubmitService, times(1)).requestPeerReview(requestTask, appUser.getUserId(), appUser);
        verify(requestService, times(1)).addActionToRequest(request,
                null,
                RequestActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_REQUESTED,
                "peerReviewer");

        verify(workflowService, times(1)).completeTask(processTaskId, Map.of(
                BpmnProcessConstants.REQUEST_ID, request.getId(),
                BpmnProcessConstants.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_OUTCOME, AviationAerCorsiaAnnualOffsettingSubmitOutcome.PEER_REVIEW_REQUIRED
        ));
    }

    @Test
    void testGetTypes() {
        List<RequestTaskActionType> types = handler.getTypes();
        assertEquals(List.of(RequestTaskActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_REQUEST_PEER_REVIEW), types);
    }
}
