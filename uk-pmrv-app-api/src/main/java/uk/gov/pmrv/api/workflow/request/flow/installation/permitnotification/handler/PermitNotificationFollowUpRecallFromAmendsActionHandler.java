package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestRecallActionHandler;

import java.util.List;

@Component
public class PermitNotificationFollowUpRecallFromAmendsActionHandler extends RequestRecallActionHandler {

    public PermitNotificationFollowUpRecallFromAmendsActionHandler(RequestTaskService requestTaskService,
                                                                   RequestService requestService,
                                                                   WorkflowService workflowService) {
        super(requestTaskService, requestService, workflowService);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RECALL_FROM_AMENDS);
    }

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RECALLED_FROM_AMENDS;
    }
}
