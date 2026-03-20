package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2SaveRegulatorReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2RegulatorReviewSubmitService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BDRS2RegulatorReviewSaveGroupDecisionActionHandlerTest {

    @InjectMocks
    private BDRS2RegulatorReviewSaveGroupDecisionActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private BDRS2RegulatorReviewSubmitService submitService;

    @Test
    void process() {
        final String processId = "processId";
        final String requestId = "requestId";
        Long requestTaskId = 1L;
        AppUser appUser = AppUser.builder().userId("userId").build();

        RequestTask requestTask = RequestTask.builder()
                .request(Request.builder().id(requestId).build())
                .type(RequestTaskType.BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT)
                .processTaskId(processId)
                .build();

        BDRS2SaveRegulatorReviewGroupDecisionRequestTaskActionPayload payload =
                BDRS2SaveRegulatorReviewGroupDecisionRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.BDRS2_SAVE_REGULATOR_REVIEW_GROUP_DECISION_PAYLOAD)
                        .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, RequestTaskActionType.BDRS2_SAVE_REGULATOR_REVIEW_GROUP_DECISION,
                appUser, payload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(submitService, times(1)).saveReviewGroupDecision(payload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.BDRS2_SAVE_REGULATOR_REVIEW_GROUP_DECISION);
    }
}
