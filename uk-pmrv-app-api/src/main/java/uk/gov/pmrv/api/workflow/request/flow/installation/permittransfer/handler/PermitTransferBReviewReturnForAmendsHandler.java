package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.mapper.PermitReviewMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.validation.PermitReviewReturnForAmendsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferBReviewService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermitTransferBReviewReturnForAmendsHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestService requestService;
    private final PermitTransferBReviewService permitTransferBReviewService;
    private final PermitReviewReturnForAmendsValidatorService permitReviewReturnForAmendsValidatorService;
    private final WorkflowService workflowService;
    private static final PermitReviewMapper permitReviewMapper = Mappers.getMapper(PermitReviewMapper.class);

    @Override
    @Transactional
    public void process(final Long requestTaskId, 
                        final RequestTaskActionType requestTaskActionType, 
                        final AppUser appUser,
                        final RequestTaskActionEmptyPayload payload) {
        
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Validate that at least one review group is 'Operator to amend'
        permitReviewReturnForAmendsValidatorService.validate((PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload());

        // Update request payload
        permitTransferBReviewService.updatePermitTransferBRequestPayload(requestTask, appUser);

        // Add request action
        this.createRequestAction(
            requestTask.getRequest(), 
            appUser,
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload()
        );

        // Close task
        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name())
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_TRANSFER_B_REVIEW_RETURN_FOR_AMENDS);
    }

    private void createRequestAction(final Request request, 
                                     final AppUser appUser,
                                     final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload) {
        
        final PermitIssuanceApplicationReturnedForAmendsRequestActionPayload requestActionPayload = permitReviewMapper
                .toPermitIssuanceApplicationReturnedForAmendsRequestActionPayload(
                    taskPayload,
                    RequestActionPayloadType.PERMIT_TRANSFER_B_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD
                );

        requestService.addActionToRequest(
            request, 
            requestActionPayload, 
            RequestActionType.PERMIT_TRANSFER_B_APPLICATION_RETURNED_FOR_AMENDS,
            appUser.getUserId()
        );
    }
}
