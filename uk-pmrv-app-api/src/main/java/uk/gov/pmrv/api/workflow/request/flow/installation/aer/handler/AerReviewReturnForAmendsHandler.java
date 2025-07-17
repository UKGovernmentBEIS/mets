package uk.gov.pmrv.api.workflow.request.flow.installation.aer.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.mapper.AerMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.validation.AerReviewReturnForAmendsValidatorService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AerReviewReturnForAmendsHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final RequestService requestService;
    private final AerReviewService aerReviewService;
    private final AerReviewReturnForAmendsValidatorService aerReviewReturnForAmendsValidatorService;
    private final WorkflowService workflowService;
    private static final AerMapper aerMapper = Mappers.getMapper(AerMapper.class);

    @Override
    @Transactional
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, RequestTaskActionEmptyPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        // Validate that at least one review group is 'Operator to amend'
        aerReviewReturnForAmendsValidatorService.validate((AerApplicationReviewRequestTaskPayload) requestTask.getPayload());

        // Update request payload
        aerReviewService.saveRequestReturnForAmends(requestTask, appUser);

        // Add AER_APPLICATION_RETURNED_FOR_AMENDS request action
        createRequestAction(requestTask.getRequest(), appUser, (AerApplicationReviewRequestTaskPayload) requestTask.getPayload());

        // Close task
        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.AER_REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name()));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AER_REVIEW_RETURN_FOR_AMENDS);
    }

    private void createRequestAction(Request request, AppUser appUser, AerApplicationReviewRequestTaskPayload taskPayload) {
        AerApplicationReturnedForAmendsRequestActionPayload requestActionPayload = aerMapper
                .toAerApplicationReturnedForAmendsRequestActionPayload(
                    taskPayload, 
                    RequestActionPayloadType.AER_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD
                );

        requestService.addActionToRequest(request, requestActionPayload, RequestActionType.AER_APPLICATION_RETURNED_FOR_AMENDS,
                appUser.getUserId());
    }
}
