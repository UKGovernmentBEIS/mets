package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.service.RequestAviationAerUkEtsReviewService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsReviewSaveGroupDecisionActionHandlerTest {

    @InjectMocks
    private AviationAerUkEtsReviewSaveGroupDecisionActionHandler saveGroupDecisionActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestAviationAerUkEtsReviewService aviationAerUkEtsReviewService;


    @Test
    void process() {
        Long requestTaskId = 1L;
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        AviationAerUkEtsSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload =
            AviationAerUkEtsSaveReviewGroupDecisionRequestTaskActionPayload.builder().build();
        PmrvUser user = PmrvUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        //invoke
        saveGroupDecisionActionHandler
            .process(requestTaskId, RequestTaskActionType.AVIATION_AER_UKETS_SAVE_REVIEW_GROUP_DECISION, user, taskActionPayload);

        //verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(aviationAerUkEtsReviewService, times(1)).saveReviewGroupDecision(taskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(saveGroupDecisionActionHandler.getTypes()).containsOnly(RequestTaskActionType.AVIATION_AER_UKETS_SAVE_REVIEW_GROUP_DECISION);
    }
}