package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.handler;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRReviewService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WasteQDRReviewSaveGroupDecisionActionHandlerTest {

    @InjectMocks
    private WasteQDRReviewSaveGroupDecisionActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WasteQDRReviewService submitService;

    @Test
    void process() {
        final long taskId = 1L;
        final AppUser user = AppUser.builder().build();
        final WasteQDRSaveReviewGroupDecisionRequestTaskActionPayload payload = WasteQDRSaveReviewGroupDecisionRequestTaskActionPayload
                .builder()
                .payloadType(RequestTaskActionPayloadType.WASTE_QDR_SAVE_REVIEW_GROUP_DECISION_PAYLOAD)
                .build();
        final String processId = "processId";
        final String requestId = "requestId";
        final RequestTask task = RequestTask.builder()
                .request(Request.builder().id(requestId).build())
                .type(RequestTaskType.WASTE_QDR_APPLICATION_REGULATOR_REVIEW_SUBMIT)
                .processTaskId(processId)
                .build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(task);

        handler.process(taskId, RequestTaskActionType.ALR_SAVE_REGULATOR_REVIEW_GROUP_DECISION, user, payload);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(submitService, times(1)).saveReviewDecision(payload, task);
    }

    @Test
    void getTypes() {
        Assertions.assertEquals(List.of(RequestTaskActionType.WASTE_QDR_SAVE_REVIEW_GROUP_DECISION), handler.getTypes());
    }
}
