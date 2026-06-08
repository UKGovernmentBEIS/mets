package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestRecallActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestRecallEmailNotificationHandler;

import java.util.List;

@Component
public class NERApplicationVerificationRecallActionHandler extends RequestRecallActionHandler {

    public NERApplicationVerificationRecallActionHandler(RequestTaskService requestTaskService, RequestService requestService,
                                                         WorkflowService workflowService, List<RequestRecallEmailNotificationHandler> handlers) {
        super(requestTaskService, requestService, workflowService, handlers);
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.NER_RECALLED_FROM_VERIFICATION;
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NER_RECALL_FROM_VERIFICATION);
    }
}
