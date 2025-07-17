package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBSaveDetailsConfirmationReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferBReviewService;

@ExtendWith(MockitoExtension.class)
class PermitTransferBSaveDetailsConfirmationReviewGroupDecisionActionHandlerTest {

    @InjectMocks
    private PermitTransferBSaveDetailsConfirmationReviewGroupDecisionActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitTransferBReviewService permitTransferBReviewService;

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_TRANSFER_B_SAVE_DETAILS_CONFIRMATION_REVIEW_GROUP_DECISION);
    }

    @Test
    void process() {
        
        final Long requestTaskId = 1L;
        final RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_TRANSFER_B_SAVE_DETAILS_CONFIRMATION_REVIEW_GROUP_DECISION;
        final AppUser appUser = AppUser.builder().build();
        final PermitTransferBSaveDetailsConfirmationReviewGroupDecisionRequestTaskActionPayload payload = 
            PermitTransferBSaveDetailsConfirmationReviewGroupDecisionRequestTaskActionPayload.builder().build();

        final RequestTask requestTask = RequestTask.builder().id(1L).build();
        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType, appUser, payload);

        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(permitTransferBReviewService, times(1)).saveDetailsConfirmationReviewGroupDecision(payload, requestTask);
    }
}
