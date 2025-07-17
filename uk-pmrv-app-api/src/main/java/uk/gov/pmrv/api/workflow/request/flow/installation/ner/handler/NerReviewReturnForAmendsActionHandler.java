package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper.NerMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplyReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NerReviewReturnForAmendsValidator;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NerReviewReturnForAmendsActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private static final NerMapper NER_MAPPER = Mappers.getMapper(NerMapper.class);
    
    private final RequestTaskService requestTaskService;
    private final RequestService requestService;
    private final NerApplyReviewService applyReviewService;
    private final NerReviewReturnForAmendsValidator amendsValidator;
    private final WorkflowService workflowService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final RequestTaskActionEmptyPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final NerApplicationReviewRequestTaskPayload taskPayload =
            (NerApplicationReviewRequestTaskPayload) requestTask.getPayload();

        // validate review groups
        amendsValidator.validate(taskPayload);

        // update request payload
        applyReviewService.updateRequestPayload(requestTask, appUser);

        // add timeline action
        this.createRequestAction(requestTask.getRequest(), appUser, taskPayload);

        // close task
        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED.name())
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NER_REVIEW_RETURN_FOR_AMENDS);
    }

    private void createRequestAction(final Request request,
                                     final AppUser appUser,
                                     final NerApplicationReviewRequestTaskPayload taskPayload) {

        final NerApplicationReturnedForAmendsRequestActionPayload requestActionPayload = NER_MAPPER
            .toNerApplicationReturnedForAmendsRequestActionPayload(taskPayload);

        requestService.addActionToRequest(
            request,
            requestActionPayload,
            RequestActionType.NER_APPLICATION_RETURNED_FOR_AMENDS,
            appUser.getUserId()
        );
    }
}
