package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBSaveDetailsConfirmationReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferBReviewService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermitTransferBSaveDetailsConfirmationReviewGroupDecisionActionHandler implements
    RequestTaskActionHandler<PermitTransferBSaveDetailsConfirmationReviewGroupDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitTransferBReviewService permitTransferBReviewService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final PermitTransferBSaveDetailsConfirmationReviewGroupDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        permitTransferBReviewService.saveDetailsConfirmationReviewGroupDecision(payload, requestTask);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_TRANSFER_B_SAVE_DETAILS_CONFIRMATION_REVIEW_GROUP_DECISION);
    }
}
