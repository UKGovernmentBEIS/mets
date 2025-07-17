package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerAuthorityResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.AuthorityResponseType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerAuthorityResponseService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NerAuthorityResponseValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NerReviewValidator;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NerAuthorityResponseNotifyOperatorActionHandler
    implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final NerReviewValidator reviewValidator;
    private final NerAuthorityResponseValidator authorityResponseValidator;
    private final NerAuthorityResponseService authorityResponseService;
    private final WorkflowService workflowService;
    
    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final NotifyOperatorForDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final NerAuthorityResponseRequestTaskPayload taskPayload =
            (NerAuthorityResponseRequestTaskPayload) requestTask.getPayload();
        final DecisionNotification decisionNotification = payload.getDecisionNotification();

        authorityResponseValidator.validateAuthorityResponse(taskPayload);
        reviewValidator.validateNotifyUsers(requestTask, decisionNotification, appUser);

        authorityResponseService.saveAuthorityDecisionNotification(requestTask, decisionNotification, appUser);
        final AuthorityResponseType authorityResponseType = taskPayload.getAuthorityResponse().getType();

        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(
                BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.AUTHORITY_RESPONSE, authorityResponseType
            )
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NER_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
