package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERSaveRegulatorReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NERRegulatorReviewSubmitService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NERRegulatorReviewSaveGroupDecisionActionHandlerTest {

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NERRegulatorReviewSubmitService submitService;

    @InjectMocks
    private NERRegulatorReviewSaveGroupDecisionActionHandler handler;

    @Test
    void process_shouldCallSubmitService() {
        // given
        Long requestTaskId = 1L;
        RequestTask requestTask = new RequestTask();
        AppUser appUser = new AppUser();
        NERSaveRegulatorReviewGroupDecisionRequestTaskActionPayload payload =
                new NERSaveRegulatorReviewGroupDecisionRequestTaskActionPayload();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        // when
        handler.process(
                requestTaskId,
                RequestTaskActionType.NER_SAVE_REGULATOR_REVIEW_GROUP_DECISION,
                appUser,
                payload
        );

        // then
        verify(requestTaskService).findTaskById(requestTaskId);
        verify(submitService).saveReviewGroupDecision(payload, requestTask);
    }

    @Test
    void getTypes_shouldReturnCorrectType() {
        // when
        List<RequestTaskActionType> result = handler.getTypes();

        // then
        assertEquals(
                List.of(RequestTaskActionType.NER_SAVE_REGULATOR_REVIEW_GROUP_DECISION),
                result
        );
    }
}
